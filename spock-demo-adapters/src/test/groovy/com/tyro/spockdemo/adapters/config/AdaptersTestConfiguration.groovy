/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.adapters.config

import com.tyro.spockdemo.adapters.AdaptersConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [AdaptersConfiguration.class, AdaptersTestMockConfiguration.class])
class AdaptersTestConfiguration {}
