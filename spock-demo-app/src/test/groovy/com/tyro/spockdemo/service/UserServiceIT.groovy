/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.exception.UserAlreadyExistsException
import com.tyro.spockdemo.model.UserModel
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.annotation.Resource
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class UserServiceIT extends Specification {

    @Resource
    private UserService userService

    def "should create a new user when calling the user service create operation"() {

        given:
        def username = 'username'
        def password = 'encryptedPassword'
        def userModel = new UserModel(username, password)

        when:
        userService.create(userModel)

        then:
        def savedUser = userService.getUser(username)
        savedUser.username == username
        savedUser.encryptedPassword == password
    }

    def "should throw an exception when attempting to create a user with an existing username"() {

        given:
        def userModel1 = new UserModel('username', 'encryptedPassword')
        def userModel2 = new UserModel('username', 'password1')
        userService.create(userModel1)

        when:
        userService.create(userModel2)

        then:
        thrown(UserAlreadyExistsException)
    }
}
