/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Entity

@Entity
class User(
        var username: String,
        var password: String,
        var firstName: String,
        var surname: String,
        var email: String

) : AbstractPersistable<Long>()
