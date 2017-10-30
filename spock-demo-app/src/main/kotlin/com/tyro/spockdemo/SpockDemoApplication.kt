/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = arrayOf("com.tyro.spockdemo.repository"))
@SpringBootApplication
class SpockDemoApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SpockDemoApplication::class.java, *args)
        }
    }
}
