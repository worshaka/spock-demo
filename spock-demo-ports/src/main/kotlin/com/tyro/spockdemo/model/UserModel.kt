/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.model

class UserModel(
        val username: String,
        val encryptedPassword: String
) {
    fun isValid() = username.isNotBlank() && encryptedPassword.isNotBlank()
}
