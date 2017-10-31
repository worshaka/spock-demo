/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.tyro.spockdemo.adapters.dto.AuthenticationDTO
import com.tyro.spockdemo.adapters.dto.UserDTO
import com.tyro.spockdemo.ports.model.UserModel
import com.tyro.spockdemo.ports.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.PUT
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/user")
@RestController
class UserController(private val userService: UserService, private val encryptionService: EncryptionService) {

    @RequestMapping(method = arrayOf(PUT))
    fun createNewUser(@RequestBody userDTO: UserDTO) {
        val userModel = userDTO.toUserModel()
        userService.create(userModel)
    }

    @RequestMapping("/authenticate", method = arrayOf(GET))
    fun authenticateUser(@RequestBody userDTO: UserDTO): AuthenticationDTO {
        val userModel = userService.getUser(userDTO.username)
        val isAuthenticated = userModel?.let { encryptionService.checkPassword(userDTO.password, it.encryptedPassword) } ?: false
        return AuthenticationDTO(isAuthenticated)
    }

    private fun UserDTO.toUserModel() = UserModel(username, encryptionService.encryptPassword(password))
}
