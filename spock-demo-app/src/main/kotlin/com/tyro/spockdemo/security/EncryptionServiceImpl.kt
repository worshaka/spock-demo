/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.security

import org.jasypt.util.password.PasswordEncryptor
import org.springframework.stereotype.Service

@Service
class EncryptionServiceImpl(private val passwordEncyptor: PasswordEncryptor) : EncryptionService {

    override fun encryptPassword(plainText: String): String = passwordEncyptor.encryptPassword(plainText)

    override fun checkPassword(plainText: String, encryptedPassword: String): Boolean = passwordEncyptor.checkPassword(plainText, encryptedPassword)
}
