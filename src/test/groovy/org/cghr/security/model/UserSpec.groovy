package org.cghr.security.model

import spock.lang.Specification


/**
 * Created by ravitej on 27/5/14.
 */
class UserSpec extends Specification {

    User user = new User()

    def setupSpec() {

    }

    def setup() {

    }

    def "should verify User to be Empty"() {

        expect:
        user.isEmpty() == true


    }

    def "should verify User to be Not Empty"() {

        given:
        user.username = 'user1'
        user.password = 'secret1'

        expect:
        user.isEmpty() == false


    }

}