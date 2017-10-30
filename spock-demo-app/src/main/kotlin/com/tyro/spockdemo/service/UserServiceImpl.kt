/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.entity.User
import com.tyro.spockdemo.repository.UserRepository
import com.tyro.spockdemo.exception.UserAlreadyExistsException
import com.tyro.spockdemo.model.UserModel
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun create(userModel: UserModel) {
        getUser(userModel.username)?.let { throw UserAlreadyExistsException() } ?: userRepository.save(userModel.toUser())
    }

    override fun getUser(username: String): UserModel? = userRepository.findByUsername(username)?.toUserModel()
}

private fun UserModel.toUser() = User(username, encryptedPassword)

private fun User.toUserModel() = UserModel(username, password)
