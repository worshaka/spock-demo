/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.google.gson.Gson
import com.tyro.spockdemo.adapters.AdapterTestBase
import com.tyro.spockdemo.adapters.dto.in.AuthenticateUserDTO
import com.tyro.spockdemo.adapters.dto.in.UpdateUserDTO
import com.tyro.spockdemo.adapters.dto.out.AuthenticationResultDTO
import com.tyro.spockdemo.adapters.dto.in.NewUserDTO
import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.exception.UserDoesNotExistException
import com.tyro.spockdemo.ports.model.UserModel
import com.tyro.spockdemo.ports.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Unroll

import javax.annotation.Resource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class UserControllerTest extends AdapterTestBase {

    @Resource
    private MockMvc mockMvc

    @Resource
    private UserService userService

    @Resource
    private EncryptionService encryptionService

    private Gson gson = new Gson()

    static createNewUserDTO(String username = 'username', String password = 'password', String firstName = 'Travis', String surname = 'Jones',
                            String email = 'default@domain.net') {

        new NewUserDTO(username, password, firstName, surname, email)
    }

    static createUpdateUserDTO() {
        new UpdateUserDTO(_ as String, _ as String, _ as String, _ as String, _ as String, _ as String)
    }

    static createUserModel(String username, String encryptedPassword) {
        new UserModel(username, encryptedPassword, _ as String, _ as String, _ as String)
    }

    def "should call the port service to create a user when the create user URI is invoked"() {

        given:
        def username = 'username'
        def plainTextPassword = 'password'
        def encryptedPassword = 'encryptedPassword'
        def firstName = 'Travis'
        def surname = 'Jones'
        def email = 'myEmail@domain.net'
        def newUserDTO = createNewUserDTO(username, plainTextPassword, firstName, surname, email)

        when: 'the createNewUser user URI is invoked with a given NewUserDTO'
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(newUserDTO)))
                .andExpect(status().isOk())

        then: 'the encryption service will encrypt the supplied plain text password'
        1 * encryptionService.encryptPassword(plainTextPassword) >> encryptedPassword

        then: 'the user service will create a new user with the given username, encrypted password and personal details'
        1 * userService.createNewUser(_ as UserModel) >> { UserModel userModel ->
            with(userModel) {
                getUsername() == username
                getEncryptedPassword() == encryptedPassword
                getFirstName() == firstName
                getSurname() == surname
                getEmail() == email
            }
        }
    }

    def "should report a HTTP 400 bad request if an exception occurs calling the create user port service"() {

        given: 'the encryption service will encrypt the supplied plain text password'
        encryptionService.encryptPassword(_ as String) >> 'encryptedPassword'

        and: 'the user service createNewUser function will throw a UserAlreadyExistsException'
        userService.createNewUser(_ as UserModel) >> { throw new UserAlreadyExistsException() }

        expect: 'a HTTP 400 bad request when attempting to create a new user with an existing username'
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createNewUserDTO())))
                .andExpect(status().is4xxClientError())
    }

    @Unroll
    def "should return the authentication status of #result when #description"() {

        given:
        def username = 'username'
        def plainTextPassword = 'password'
        def authenticateUserDTO = new AuthenticateUserDTO(username, plainTextPassword)

        when: 'the authenticate URI is invoked with a given userDTO'
        def mvcResult = mockMvc.perform(get('/api/v1/user/authenticate')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(authenticateUserDTO)))
                .andExpect(status().isOk())
                .andReturn()

        then: 'the user service will retrieve the user from the application for the given username'
        1 * userService.getUser(username) >> user

        then: 'the encryption service will check the given plain text password with the stored encrypted password'
        numberOfCheckPasswordCalls * encryptionService.checkPassword(plainTextPassword, user?.encryptedPassword ?: _) >> result

        and: 'the authenticate result is returned'
        def authenticationResult = gson.fromJson(mvcResult.response.contentAsString, AuthenticationResultDTO.class)
        authenticationResult.isAuthenticated == result

        where:
        user                                     | numberOfCheckPasswordCalls | result | description
        createUserModel('username', 'correct')   | 1                          | true   | 'the username and password are correct'
        createUserModel('username', 'incorrect') | 1                          | false  | 'the username is correct but the password is incorrect'
        null                                     | 0                          | false  | 'the username does not exist'
    }

    def "should update a user with new details"() {

        given:
        def updateUserDTO = new UpdateUserDTO(existingUsername, newUsername, newPassword, newFirstName, newSurname, newEmail)
        def newEncryptedPassword = 'encryptedPassword'

        when:
        mockMvc.perform(post('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updateUserDTO)))
                .andExpect(status().isOk())
                .andReturn()

        then:
        1 * encryptionService.encryptPassword(newPassword) >> newEncryptedPassword

        then:
        1 * userService.updateUser(_ as String, _ as UserModel) >> { String currentUsername, UserModel userModel ->
            currentUsername == existingUsername
            with(userModel) {
                username == newUsername
                encryptedPassword == newEncryptedPassword
                firstName == newFirstName
                surname == newSurname
                email == newEmail
            }
        }

        where:
        existingUsername | newUsername | newPassword   | newFirstName | newSurname | newEmail
        'user'           | 'newUser'   | 'newPassword' | 'John'       | 'Snow'     | 'jsnow@thewall.org'
        'user'           | 'user'      | 'password'    | 'Travis'     | 'Jones'    | 'tjones@tyro.com'
    }

    def "should report a HTTP 400 bad request if an exception occurs calling the update user port service"() {

        given: 'the encryption service will encrypt the supplied plain text password'
        encryptionService.encryptPassword(_ as String) >> 'encryptedPassword'

        and: 'the user service updateUser function will throw a UserDoesNotExistException'
        userService.updateUser(_ as String, _ as UserModel) >> { throw new UserDoesNotExistException() }

        expect: 'a HTTP 400 bad request when attempting to update an existing user'
        mockMvc.perform(post('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createUpdateUserDTO())))
                .andExpect(status().is4xxClientError())
    }
}
