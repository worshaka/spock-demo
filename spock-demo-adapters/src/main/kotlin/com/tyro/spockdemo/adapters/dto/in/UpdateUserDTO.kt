/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.dto.`in`

class UpdateUserDTO(
        val existingUsername: String,
        val username: String,
        val password: String,
        val firstName: String,
        val surname: String,
        val email: String
)
