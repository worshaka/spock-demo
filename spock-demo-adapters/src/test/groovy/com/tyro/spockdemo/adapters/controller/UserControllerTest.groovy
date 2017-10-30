/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.google.gson.Gson
import com.tyro.spockdemo.adapters.AdapterTestBase
import com.tyro.spockdemo.adapters.dto.UserDTO
import com.tyro.spockdemo.model.UserModel
import com.tyro.spockdemo.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import javax.annotation.Resource

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

    def "should save a user when the save user uri is invoked"() {

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
}
