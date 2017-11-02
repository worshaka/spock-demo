/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.ports.service

import com.tyro.spockdemo.ports.security.EncryptionService
import org.jasypt.util.password.PasswordEncryptor
import org.springframework.stereotype.Service

@Service
class EncryptionServiceImpl(private val passwordEncryptor: PasswordEncryptor) : EncryptionService {

    override fun encryptPassword(plainText: String): String = passwordEncryptor.encryptPassword(plainText)

    override fun checkPassword(plainText: String, encryptedPassword: String): Boolean = passwordEncryptor.checkPassword(plainText, encryptedPassword)
}
