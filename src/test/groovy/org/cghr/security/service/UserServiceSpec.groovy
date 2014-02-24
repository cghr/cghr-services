package org.cghr.security.service

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
@ContextConfiguration(locations = "classpath:spring-context.xml")
class UserServiceSpec extends Specification {


    UserService userService
    @Shared
    def authtoken = "ABCDEFG-12345"

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    DbAccess mockDbAccess
    DbStore mockDbStore


    @Shared
    User validUser = new User(username: 'user1', password: 'secret1')
    @Shared
    User invalidUser = new User(username: 'invalidUser', password: 'secret1')
    @Shared
    User validUserWithBadPassword = new User(username: 'user1', password: 'badpassword')
    @Shared String hostname="localhost"


    def setup() {

        mockDbAccess = Stub() {
            getRowAsMap("select * from user where username=?", [validUser.username]) >> [id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active']
            getRowAsMap("select * from user where username=?", [invalidUser.username]) >> [:]
            getRowAsJson("select * from user where username=?", [validUser.username]) >> '{"id":1,"username":"user1","password":"secret1","role":"user","status":"active"}'
            getRowAsJson("select * from user where username=?", [invalidUser.username]) >> '{}'
            hasRows("select * from authtoken where token=?", [authtoken]) >> true
        }

        mockDbStore = Mock()
        def userMap = [id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active']
        mockDbStore.saveOrUpdate(userMap, 'user') >> {
            gSql.executeInsert("insert into user(id,username,password,role,status) values(?,?,?,?,?)", [
                    userMap.id,
                    userMap.username,
                    userMap.password,
                    userMap.role,
                    userMap.status
            ])
        }
        def authTokenMap = [token: _, username: 'user1', role: 'user']
        mockDbStore.saveOrUpdate(_, "authtoken") >> {
            gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)", [
                    "some random token",
                    authTokenMap.username,
                    authTokenMap.role
            ])
        }
        def successUserlog = [username: validUser.username, status: "success"]
        mockDbStore.saveOrUpdate(successUserlog, "userlog") >> {
            gSql.executeInsert("insert into userlog(username,status) values(?,?)", [
                    successUserlog.username,
                    successUserlog.status
            ])
        }
        def failUserlog = [username: invalidUser.username, status: "fail"]
        mockDbStore.saveOrUpdate(failUserlog, "userlog") >> {
            gSql.executeInsert("insert into userlog(username,status) values(?,?)", [
                    failUserlog.username,
                    failUserlog.status
            ])
        }


        OnlineAuthService mockOnlineAuthService = Stub() {

            authenticate(validUser,hostname) >> new User(id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active')
            authenticate(invalidUser,hostname) >> {throw new NoSuchUserFound()}
        }




        userService = new UserService(mockDbAccess, mockDbStore, mockOnlineAuthService)
        userService.onlineAuthService = mockOnlineAuthService

        dt.cleanInsert("user,authtoken,userlog")
    }

    def "should validate a given user in online mode"() {

        given:
        dt.clean('user')

        expect:
        userService.isValid(user,hostname) == result

        where:
        user                     || result
        validUser                || true
        invalidUser              || false
        validUserWithBadPassword || false

    }


    def "should authenticate a valid user locally when server not found"() {

        given:

        OnlineAuthService onlineAuthServiceOffline = Mock()
        onlineAuthServiceOffline.authenticate(validUser) >> {
            throw new ServerNotFoundException("connection to server failed")
        } //Offline Mode No server available

        UserService userServiceOffline = new UserService(mockDbAccess, mockDbStore, onlineAuthServiceOffline)

        expect:
        userServiceOffline.isValid(validUser,hostname) == true

    }

    def "should get an User as Json"() {

        expect:
        userService.getUserJson(user) == json

        where:
        user        || json
        validUser   || '{"id":1,"username":"user1","password":"secret1","role":"user","status":"active"}'
        invalidUser || '{}'
    }


    def "should get a valid user cookie  json"() {

        expect:
        userService.getUserCookieJson(user) == result

        where:
        user      || result
        validUser || '{"username":"user1","role":{"title":"user","bitMask":2}}'

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
        User user = new User(id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active')


        when:
        userService.cacheUserLocally(user)

        then:
        gSql.firstRow("select * from user where id=?", [1]) == [id: 1, username: "user1", password: "secret1", role: "user", status: "active"]
    }

    def "should get a valid user as Map"() {
        expect:
        userService.getUserAsMap(user) == result

        where:
        user        || result
        validUser   || [id: 1, username: "user1", password: "secret1", role: "user", status: "active"]
        invalidUser || [:]
    }

    def "should be true for a valid token"() {
        given:
        gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)", [authtoken, 'user1', 'user'])


        expect:
        userService.isValidToken(authtoken) == true
    }
}
