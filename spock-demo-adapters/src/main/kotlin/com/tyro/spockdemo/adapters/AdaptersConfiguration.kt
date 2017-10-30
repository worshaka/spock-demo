/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@ComponentScan(value = *arrayOf("com.tyro.spockdemo.adapters.controller"))
@Configuration
@EnableWebMvc
class AdaptersConfiguration
