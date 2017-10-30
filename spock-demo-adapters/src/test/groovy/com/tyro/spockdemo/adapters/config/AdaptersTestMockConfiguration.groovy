/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.config

import com.tyro.spockdemo.security.EncryptionService
import com.tyro.spockdemo.service.UserService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import spock.mock.DetachedMockFactory

@TestConfiguration
class AdaptersTestMockConfiguration {

    private DetachedMockFactory mockFactory = new DetachedMockFactory()

    @Bean
    UserService userService() {
        mockFactory.Mock(UserService)
    }

    @Bean
    EncryptionService encryptionService() {
        mockFactory.Mock(EncryptionService)
    }
}
