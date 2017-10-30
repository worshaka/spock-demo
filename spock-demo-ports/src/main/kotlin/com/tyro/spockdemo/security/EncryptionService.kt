/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.security

interface EncryptionService {

    fun encryptPassword(plainText: String): String

    fun checkPassword(plainText: String, encryptedPassword: String): Boolean
}
