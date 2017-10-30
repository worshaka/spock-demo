/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.google.gson.Gson
import com.tyro.spockdemo.adapters.AdapterTestBase
import com.tyro.spockdemo.adapters.dto.AuthenticationDTO
import com.tyro.spockdemo.adapters.dto.UserDTO
import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.model.UserCredentialsModel
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
    private UserService userService

    @Resource
    private EncryptionService encryptionService

    @Resource
    private MockMvc mockMvc

    private Gson gson = new Gson()

    def "should call the port service to create a user when the create user URI is invoked"() {

        given:
        def username = 'username'
        def plainTextPassword = 'password'
        def userDTO = new UserDTO(username, plainTextPassword)
        def encryptedPassword = 'encrypted'
        encryptionService.encryptPassword(plainTextPassword) >> encryptedPassword

        when:
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userDTO)))
                .andExpect(status().isOk())

        then:
        1 * userService.create({ it.username == username && it.encryptedPassword == encryptedPassword } as UserModel)
    }

    def "should report a HTTP 400 bad request if an exception occurs calling the create user port service"() {

        given:
        userService.create(_ as UserModel) >> { throw new UserAlreadyExistsException() }
        encryptionService.encryptPassword(_ as String) >> "encryptedPassword"

        expect:
        mockMvc.perform(put('/api/v1/user')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new UserDTO('username', 'password'))))
                .andExpect(status().is4xxClientError())
    }

    @Unroll
    def "should return the correct authentication status for a user with #password authentication details"() {

        given:
        encryptionService.encryptPassword(password) >> encryptedPassword
        userService.authenticate(_ as UserCredentialsModel) >> result

        expect:
        def mvcResult = mockMvc.perform(get('/api/v1/user/authenticate')
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new UserDTO(username, password))))
                .andExpect(status().isOk())
                .andReturn()

        def authenticationResult = gson.fromJson(mvcResult.response.contentAsString, AuthenticationDTO.class)

        authenticationResult.isAuthenticated == result

        where:
        username | password    | encryptedPassword  |   result
        'user1'  | 'correct'   | 'encrypted1'       |   true
        'user2'  | 'incorrect' | 'encrypted2'       |   false
    }
}
