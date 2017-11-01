/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.model.UserModel
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

        given: 'a new user'
        def username = 'username'
        def password = 'encryptedPassword'
        def userModel = new UserModel(username, password)

        when: 'the new user is created'
        userService.create(userModel)

        then: 'the user can be retrieved with the correct username and password fields'
        def savedUser = userService.getUser(username)
        with(savedUser) {
            this.username == username
            this.encryptedPassword == password
        }
    }

    def "should throw an exception when attempting to create a user with an existing username"() {

        given: 'an existing user'
        def username = 'username'
        userService.create(new UserModel('username', 'password1'))

        when: 'attempting to create a new user with an existing username'
        userService.create(new UserModel(username, 'differentPassword'))

        then: 'a UserAlreadyExistsException is thrown'
        thrown(UserAlreadyExistsException)
    }
}
