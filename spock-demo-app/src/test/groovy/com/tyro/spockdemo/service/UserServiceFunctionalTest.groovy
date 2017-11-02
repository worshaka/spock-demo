/*
 * Copyright (c) 2003 - 2017 Tyro Payments Limited.
 * Lv1, 155 Clarence St, Sydney NSW 2000.
 * All rights reserved.
 */
package com.tyro.spockdemo.service

import com.tyro.spockdemo.ports.exception.UserAlreadyExistsException
import com.tyro.spockdemo.ports.exception.UserDoesNotExistException
import com.tyro.spockdemo.ports.model.UserModel
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class UserServiceFunctionalTest extends Specification {

    @Resource
    private UserService userService

    static UserModel createUserModel(String username = 'username', String password = 'encryptedPassword', String firstName = 'Travis', String surname = 'Jones',
                                     String email = 'fake@domain.net') {

        new UserModel(username, password, firstName, surname, email)
    }

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

    def "should update a user with new details"() {

        given: 'an existing user'
        userService.createNewUser(createUserModel())

        when: 'a call to update an existing user'
        userService.updateUser(existingUsername, createUserModel(newUsername, newEncryptedPassword, newFirstName, newSurname, newEmail))

        then: 'the user details are updated'
        with(userService.getUser(newUsername)) {
            username == newUsername
            encryptedPassword == newEncryptedPassword
            firstName == newFirstName
            surname == newSurname
            email == newEmail
        }

        where:
        existingUsername    |   newUsername |   newEncryptedPassword    |   newFirstName    |   newSurname  |   newEmail
        'username'          |   'newUser'   |   'newPassword'           |   'John'          |   'Snow'      |   'jsnow@thewall.org'
        'username'          |   'username'  |   'password'              |   'Travis'        |   'Jones'     |   'tjones@tyro.com'
    }

    def "should throw a UserDoesNotExistException when the username doesn't exist"() {

        when: 'attempting to update a user that does not exist'
        userService.updateUser('missingUser', createUserModel())

        then: 'expect a UserDoesNotExistException to be thrown'
        thrown(UserDoesNotExistException)
    }
}
