/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.controller

import com.tyro.spockdemo.adapters.dto.UserDTO
import com.tyro.spockdemo.model.UserModel
import com.tyro.spockdemo.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

    private fun UserDTO.toUserModel() = UserModel(username, encryptionService.encryptPassword(password))
}
