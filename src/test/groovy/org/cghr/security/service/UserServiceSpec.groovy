package org.cghr.security.service

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.security.model.User
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class UserServiceSpec extends Specification {


    @Autowired
    UserService userService
    @Shared
    def authtoken = "ABCDEFG-12345"

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore


    @Shared
    User validUser = new User(username: 'user1', password: 'secret1')
    @Shared
    User invalidUser = new User(username: 'invalidUser', password: 'secret1')
    @Shared
    User validUserWithBadPassword = new User(username: 'user1', password: 'badpassword')
    @Shared
    String hostname = "localhost"
    @Shared
    HttpClientErrorException httpClientErrorException
    @Shared
    ResourceAccessException resourceAccessException

    def setupSpec() {
        httpClientErrorException = Mock()
    }

    def setup() {

        OnlineAuthService mockOnlineAuthService = Stub() {

            authenticate(validUser) >> [id: 1, username: 'user1', password: 'secret1', role: [title: 'user', bitMask: 2]]
            authenticate(invalidUser) >> { throw httpClientErrorException }
            getServerAuthUrl() >> "http://dummyServer:8080/app/api/security/auth"
        }

        userService.onlineAuthService = mockOnlineAuthService
        dt.cleanInsert("user,authtoken,userlog")
    }

    def "should validate a given user in online mode"() {

        given:
        dt.clean('user')

        expect:
        userService.isValid(user, hostname) == result

        where:
        user                     || result
        validUser                || true
        invalidUser              || false
        validUserWithBadPassword || false

    }


    def "should authenticate a valid user locally when server not found"() {

        given:

        OnlineAuthService onlineAuthServiceOffline = Mock()

        onlineAuthServiceOffline.authenticate(validUser, 'localhost') >> {
            throw new ResourceAccessException("Server not found")
        } //Offline Mode No server available
        onlineAuthServiceOffline.getServerAuthUrl() >> "http://dummyServer:8080/app/api/security/auth"

        UserService userServiceOffline = new UserService(dbAccess, dbStore, onlineAuthServiceOffline)

        expect:
        userServiceOffline.isValid(validUser, hostname) == true

    }

    def "should not authenticate an invalid user  when User is not Existant"() {

        given:

        OnlineAuthService onlineAuthServiceOffline = Mock()
        User randomUser = new User([username: 'randomuser', password: 'randompassword'])
        onlineAuthServiceOffline.authenticate(randomUser, 'localhost') >> {
            throw httpClientErrorException
        }
        onlineAuthServiceOffline.getServerAuthUrl() >> "http://dummyServer:8080/app/api/security/auth"

        UserService userServiceOffline = new UserService(dbAccess, dbStore, onlineAuthServiceOffline)

        expect:
        userServiceOffline.isValid(randomUser, hostname) == false

    }

    def "should get an User as Json"() {

        expect:
        userService.getUserJson(user) == json

        where:
        user        || json
        validUser   || '{"id":1,"username":"user1","password":"secret1","role":"user"}'
        invalidUser || '{}'
    }


    def "should get a valid user cookie  json"() {

        expect:
        userService.getUserCookieJson(user) == result

        where:
        user      || result
        validUser || '{"id":1,"username":"user1","password":"secret1","role":{"title":"user","bitMask":2}}'

    }

    def "should get user id from an User object"() {

        expect:
        userService.getId(validUser) == "1"
    }

    def "should save authtoken to database for an user successful login"() {
        given:
        def authtoken = 'c88044fd-058c-421f-92a1-328c2b560e0e'

        when:
        userService.saveAuthToken(authtoken, validUser)
        def rows = gSql.rows("select token,username,role from authtoken")

        then:
        rows.size() == 1
    }

    def "should log user authentication with success status"() {

        when:
        userService.logUserAuthStatus(user, loginStatus)

        then:
        gSql.firstRow("select username,status from userlog") == result

        where:
        user        | loginStatus || result
        validUser   | "success"   || [username: 'user1', status: 'success']
        invalidUser | "fail"      || [username: 'invalidUser', status: 'fail']
    }

    def "should be true for a valid local(database) user"() {

        expect:
        userService.isValidLocalUser(user) == result

        where:
        user        || result
        validUser   || true
        invalidUser || false
    }


    def "should cache(save to database) a userJson got from a http response as String"() {
        given:
        Map user = [id: 1, username: 'user1', password: 'secret1', role: [title: 'user', bitMask: 2]]


        when:
        userService.cacheUserLocally(user)

        then:
        gSql.firstRow("select id,username,password,role from user where id=?", [1]) == [id: 1, username: "user1", password: "secret1", role: "user"]
    }

    def "should get a valid user as Map"() {
        expect:
        userService.getUserAsMap(user) == result

        where:
        user        || result
        validUser   || [id: 1, username: "user1", password: "secret1", role: "user"]
        invalidUser || [:]
    }

    def "should be true for a valid token"() {
        given:
        gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)", [authtoken, 'user1', 'user'])


        expect:
        userService.isValidToken(token) == result

        where:
        token          || result
        authtoken      || true
        'random-token' || false
    }

    def "should get bit Mask user roles"() {
        expect:
        userService.getBitMask(role) == result

        where:
        role          | result
        'public'      | 1
        'user'        | 2
        'manager'     | 4
        'coordinator' | 8
        'admin'       | 16

    }

    def "should verify for serverHost"() {

        expect:
        userService.isServerHost(host, serverAuthUrl) == result
        where:
        host                 | serverAuthUrl                             || result
        "localhost"          | "http://barshi.vm-host.net:8080/hcServer" || false
        "barshi.vm-host.net" | "http://barshi.vm-host.net:8080/hcServer" || true


    }
}
