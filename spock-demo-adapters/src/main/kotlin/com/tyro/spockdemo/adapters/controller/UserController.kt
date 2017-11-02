/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.tyro.spockdemo.adapters.dto.`in`.AuthenticateUserDTO
import com.tyro.spockdemo.adapters.dto.out.AuthenticationResultDTO
import com.tyro.spockdemo.adapters.dto.`in`.NewUserDTO
import com.tyro.spockdemo.adapters.dto.`in`.UpdateUserDTO
import com.tyro.spockdemo.ports.model.UserModel
import com.tyro.spockdemo.ports.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestMethod.PUT
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/user")
@RestController
class UserController(private val userService: UserService, private val encryptionService: EncryptionService) {

    private fun NewUserDTO.toUserModel() = UserModel(
            username,
            encryptionService.encryptPassword(password),
            firstName,
            surname,
            email
    )

    private fun UpdateUserDTO.toUserModel() = UserModel(
            username,
            encryptionService.encryptPassword(password),
            firstName,
            surname,
            email
    )

    @RequestMapping("/authenticate", method = arrayOf(GET))
    fun authenticateUser(@RequestBody userDTO: AuthenticateUserDTO): AuthenticationResultDTO {
        val userModel = userService.getUser(userDTO.username)
        val isAuthenticated = userModel?.let { encryptionService.checkPassword(userDTO.password, it.encryptedPassword) } ?: false
        return AuthenticationResultDTO(isAuthenticated)
    }

    @RequestMapping(method = arrayOf(PUT))
    fun createNewUser(@RequestBody newUserDTO: NewUserDTO) {
        val userModel = newUserDTO.toUserModel()
        userService.createNewUser(userModel)
    }

    @RequestMapping(method = arrayOf(POST))
    fun updateUser(@RequestBody updateUserDTO: UpdateUserDTO) {
        userService.updateUser(updateUserDTO.existingUsername, updateUserDTO.toUserModel())
    }
}
