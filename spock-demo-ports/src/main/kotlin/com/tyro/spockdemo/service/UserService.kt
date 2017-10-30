/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.model.UserModel

interface UserService {

    fun create(userModel: UserModel)

    fun getUser(username: String): UserModel?
}
