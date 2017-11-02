/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.google.gson.Gson
import com.tyro.spockdemo.adapters.AdapterTestBase
import com.tyro.spockdemo.adapters.dto.in.AuthenticateUserDTO
import com.tyro.spockdemo.adapters.dto.out.AuthenticationResultDTO
import com.tyro.spockdemo.adapters.dto.in.NewUserDTO
import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.model.UserModel
import com.tyro.spockdemo.ports.security.EncryptionService
import com.tyro.spockdemo.ports.service.UserService
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Unroll

import javax.annotation.Resource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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

    def "should call the port service to create a user when the create user URI is invoked"() {

        given:
        def username = 'username'
        def plainTextPassword = 'password'
        def encryptedPassword = 'encryptedPassword'
        def firstName = 'Travis'
        def surname = 'Jones'
        def email = 'myEmail@domain.net'
        def userDTO = createNewUserDTO(username, plainTextPassword, firstName, surname, email)

        when: 'the createNewUser user URI is invoked with a given userDTO'
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userDTO)))
                .andExpect(status().isOk())

        then: 'the encryption service will encrypt the supplied plain text password'
        1 * encryptionService.encryptPassword(plainTextPassword) >> encryptedPassword

        then: 'the user service will create a new user with the given username, encrypted password and personal details'
        1 * userService.createNewUser({ UserModel u ->
            u.username == username &&
                    u.encryptedPassword == encryptedPassword &&
                    u.firstName == firstName &&
                    u.surname == surname &&
                    u.email == email
        })
    }

    def "should report a HTTP 400 bad request if an exception occurs calling the create user port service"() {

        when: 'the createNewUser user URI is invoked with a given userDTO containing a username that already exists'
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createNewUserDTO())))
                .andExpect(status().is4xxClientError())

        then: 'the encryption service will encrypt the supplied plain text password'
        1 * encryptionService.encryptPassword(_ as String) >> "encryptedPassword"

        then: 'the user service createNewUser function will throw a UserAlreadyExistsException'
        1 * userService.createNewUser(_ as UserModel) >> { throw new UserAlreadyExistsException() }
    }

    @Unroll
    def "should return the authentication status of #result for a user with the #user.getEncryptedPassword() password"() {

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
        user                                     | numberOfCheckPasswordCalls | result
        createUserModel('username', 'correct')   | 1                          | true
        createUserModel('username', 'incorrect') | 1                          | false
        null                                     | 0                          | false
    }

    static createNewUserDTO(String username = 'username', String password = 'password', String firstName = 'Travis',
                            String surname = 'Jones', String email = 'default@domain.net') {
        new NewUserDTO(username, password, firstName, surname, email)
    }

    static createUserModel(String username, String encryptedPassword) {
        new UserModel(username, encryptedPassword, _ as String, _ as String, _ as String)
    }
}
