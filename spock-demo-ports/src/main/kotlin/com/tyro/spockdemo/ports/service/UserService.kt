/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.ports.service

import com.tyro.spockdemo.ports.model.UserModel

interface UserService {

    fun createNewUser(userModel: UserModel)

    fun getUser(username: String): UserModel?
}
