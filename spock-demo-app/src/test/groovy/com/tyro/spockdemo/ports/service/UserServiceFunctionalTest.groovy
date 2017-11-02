/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.ports.service

import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.model.UserModel
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.annotation.Resource
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class UserServiceFunctionalTest extends Specification {

    @Resource
    private UserService userService

    def "should create a new user when calling the user service create operation"() {

        given: 'a new user'
        def username = 'username'
        def password = 'encryptedPassword'
        def firstName = 'Travis'
        def surname = 'Jones'
        def email = 'fake@domain.net'
        def userModel = createUserModel(username, password, firstName, surname, email)

        when: 'the new user is created'
        userService.createNewUser(userModel)

        then: 'the user can be retrieved with the correct field values'
        with(userService.getUser(username)) {
            getUsername() == username
            getEncryptedPassword() == password
            getFirstName() == firstName
            getSurname() == surname
            getEmail() == email
        }
    }

    def "should throw an exception when attempting to create a user with an existing username"() {

        given: 'an existing user'
        def username = 'username'
        userService.createNewUser(createUserModel(username))

        when: 'attempting to create a new user with an existing username'
        userService.createNewUser(createUserModel(username))

        then: 'a UserAlreadyExistsException is thrown'
        thrown(UserAlreadyExistsException)
    }

    static UserModel createUserModel(String username = 'username', String password = 'encryptedPassword', String firstName = 'Travis', String surname = 'Jones',
                                     String email = 'fake@domain.net') {

        new UserModel(username, password, firstName, surname, email)
    }
}
