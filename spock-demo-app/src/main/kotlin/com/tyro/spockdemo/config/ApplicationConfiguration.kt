/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.config

import org.jasypt.util.password.BasicPasswordEncryptor
import org.jasypt.util.password.PasswordEncryptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun passwordEncryptor(): PasswordEncryptor = BasicPasswordEncryptor()
}
