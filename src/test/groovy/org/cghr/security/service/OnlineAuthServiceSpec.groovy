package org.cghr.security.service

import com.google.gson.Gson
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        HttpEntity<String> request=new HttpEntity<String>(new Gson().toJson(validUser),headers)

        mockServerAuthUrl = "http://dummyServer:8080/app/api/security/auth"
        mockRestTemplate = Stub() {
            postForObject(mockServerAuthUrl,request, Map.class) >> {
               [id: 1, username: 'user1', password: 'secret1', role:[title:'user',bitMask:2], status: 'active']}
            getMessageConverters() >> [] //Mock list for adding message convertors
        }

        onlineAuthService = new OnlineAuthService(mockServerAuthUrl, mockRestTemplate)
    }

    def "should return User information for a valid User"() {
        given:
        Map actual = onlineAuthService.authenticate(validUser,"localhost")
        Map expected = [id: 1, username: 'user1', password: 'secret1', role:[title:'user',bitMask:2], status: 'active']

        expect:
        ReflectionAssert.assertReflectionEquals(expected, actual)

    }


    def "should throw exception for invalid user"() {

        given:
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        HttpEntity<String> request=new HttpEntity<String>(new Gson().toJson(invalidUser),headers)

        RestTemplate fakeRestTemplate = Stub(){
        postForObject(mockServerAuthUrl, request, Map.class) >> {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN)
        }
        getMessageConverters() >> []
        }
        OnlineAuthService fakeService = new OnlineAuthService(mockServerAuthUrl, fakeRestTemplate)

        when:
        fakeService.authenticate(invalidUser,"localhost")

        then:
        thrown(NoSuchUserFound)
    }


    def "should throw an exception when server not found"() {

        given:

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        HttpEntity<String> request=new HttpEntity<String>(new Gson().toJson(validUser),headers)

        RestTemplate fakeRestTemplate = Mock()
        fakeRestTemplate.postForObject(mockServerAuthUrl, request, Map.class) >> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
        }
        fakeRestTemplate.getMessageConverters() >> []
        OnlineAuthService fakeService = new OnlineAuthService(mockServerAuthUrl, fakeRestTemplate)

        when:
        fakeService.authenticate(validUser,"localhost")

        then:
        thrown(ServerNotFoundException)
    }

    def "should throw an ServerNotFound exception when hostname and ServerAuth Hostname are same"() {

        given:
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        HttpEntity<String> request=new HttpEntity<String>(new Gson().toJson(validUser),headers)

        RestTemplate fakeRestTemplate = Mock()
        OnlineAuthService fakeService = new OnlineAuthService(mockServerAuthUrl, fakeRestTemplate)

        when:
        fakeService.authenticate(validUser,"dummyServer")

        then:
        thrown(ServerNotFoundException)

    }

}
