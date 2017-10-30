/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters

import com.tyro.spockdemo.adapters.config.AdaptersTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [AdaptersTestConfiguration.class])
@SpringBootTest
@WebAppConfiguration
class AdapterTestBase extends Specification {}
