package org.cghr.security.service

import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.unitils.reflectionassert.ReflectionAssert
import spock.lang.Shared
import spock.lang.Specification

class OnlineAuthServiceSpec extends Specification {


    OnlineAuthService onlineAuthService
    RestTemplate mockRestTemplate
    String mockServerAuthUrl

    @Shared User validUser = new User(username: 'user1', password: 'secret1')
    @Shared User invalidUser = new User(username: 'invaliduser', password: 'secret1')

    def setupSpec() {
    }

    def setup() {

        mockServerAuthUrl = "http://dummyServer:8080/hc/api/security/auth"
        mockRestTemplate = Stub() {
            postForObject(mockServerAuthUrl, validUser, User.class) >> new User(id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active')
            getMessageConverters() >> [] //Mock list for adding message convertors
        }

        onlineAuthService = new OnlineAuthService(mockServerAuthUrl, mockRestTemplate)
    }

    def "should return User information for a valid User"() {
        given:
        User actual = onlineAuthService.authenticate(validUser)
        User expected = new User(id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active')

        expect:
        ReflectionAssert.assertReflectionEquals(expected, actual)

    }


    def "should throw exception for invalid user"() {

        given:
        RestTemplate fakeRestTemplate = Mock()
        fakeRestTemplate.postForObject(mockServerAuthUrl, invalidUser, User.class) >> {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED)
        }
        fakeRestTemplate.getMessageConverters() >> []
        OnlineAuthService fakeService = new OnlineAuthService(mockServerAuthUrl, fakeRestTemplate)

        when:
        fakeService.authenticate(invalidUser)

        then:
        thrown(NoSuchUserFound)
    }


    def "should throw an exception when server not found"() {

        given:
        RestTemplate fakeRestTemplate = Mock()
        fakeRestTemplate.postForObject(mockServerAuthUrl, validUser, User.class) >> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
        }
        fakeRestTemplate.getMessageConverters() >> []
        OnlineAuthService fakeService = new OnlineAuthService(mockServerAuthUrl, fakeRestTemplate)

        when:
        fakeService.authenticate(validUser)

        then:
        thrown(ServerNotFoundException)
    }
}
