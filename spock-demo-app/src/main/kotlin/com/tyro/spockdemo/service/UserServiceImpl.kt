/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.entity.User
import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.model.UserModel
import com.tyro.spockdemo.repository.UserRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    @Throws(UserAlreadyExistsException::class)
    override fun createNewUser(userModel: UserModel) {
        getUser(userModel.username)?.let { throw UserAlreadyExistsException() } ?: userRepository.save(userModel.toUser())
    }

    override fun getUser(username: String): UserModel? = userRepository.findByUsername(username)?.toUserModel()
}

private fun UserModel.toUser() = User(username, encryptedPassword, firstName, surname, email)

private fun User.toUserModel() = UserModel(username, password, firstName, surname, email)
