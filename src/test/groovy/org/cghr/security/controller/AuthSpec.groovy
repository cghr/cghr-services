package org.cghr.security.controller
import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.security.model.User
import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class AuthSpec extends Specification {


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    Auth auth
    @Shared
    User validUser = new User(username: 'user1', password: 'secret1')
    @Shared
    User invalidUser = new User(username: 'invaliduser', password: 'invalidpassword')
    @Shared
    User manager = new User(username: 'user4', password: 'secret4')
    @Shared
    List dataSet

    def setupSpec() {

        dataSet = new MockData().sampleData.get("user")
    }

    def setup() {
        def authtoken = "ABCDEDGH-12345"
        //Mocking User Service
        UserService mockUserService = Stub() {
            isValid(validUser) >> true
            isValid(invalidUser) >> false
            isValid(manager) >> true

            getUserCookieJson(validUser) >> '{"username":"user1","role":{"title":"user","bitMask":2}}'
            getUserCookieJson(invalidUser) >> '{}'
            getUserCookieJson(manager) >> '{"username":"user4","role":{"title":"manager","bitMask":3}}'

            getId(validUser) >> "1"
            getId(validUser) >> null
            getId(manager) >> "4"


            logUserAuthStatus(validUser, "success") >> {
                gSql.executeInsert("insert into userlog(username,status) values(?,?)", [
                        validUser.username,
                        "success"
                ])
            }
            logUserAuthStatus(invalidUser, "fail") >> {
                gSql.executeInsert("insert into userlog(username,status) values(?,?)", [invalidUser.username, "fail"])
            }
            logUserAuthStatus(manager, "success") >> {
                gSql.executeInsert("insert into userlog(username,status) values(?,?)", [manager.username, "success"])
            }

            getUserJson(validUser) >> '{"id":1,"username":"user1","password":"secret1","role":"user","status":"active"}'
            getUserJson(invalidUser) >> '{}'
            getUserJson(manager) >> '{"id":4,"username":"user4","password":"secret4","role":"manager","status":"active"}'

            saveAuthToken(_, validUser) >> {
                gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)", [
                        authtoken,
                        validUser.username,
                        'user'
                ])
            }

            saveAuthToken(_, manager) >> {
                gSql.executeInsert("insert into authtoken(username,token,role) values(?,?,?)", [
                        authtoken,
                        manager.username,
                        'manager'
                ])
            }
        }
        auth = new Auth(mockUserService)


        dt.cleanInsert("user")
        dt.clean("authtoken")
        dt.clean("userlog")

    }

    def "should verify http responses for valid and invalid users"() {

        given:
        MockHttpServletResponse response = new MockHttpServletResponse()

        when:
        def actualJsonResp = auth.authenticate(user, response)

        then:
        response.status == httpStatus
        actualJsonResp == expectedJsonResp

        where:
        user        | httpStatus                   | expectedJsonResp
        validUser   | HttpStatus.OK.value()        | new Gson().toJson(dataSet[0])
        invalidUser | HttpStatus.FORBIDDEN.value() | "{}"
        manager     | HttpStatus.OK.value()        | new Gson().toJson(dataSet[3])


    }

    def "should verify  response cookies for valid and invalid users"() {
        given:
        MockHttpServletResponse response = new MockHttpServletResponse()

        when:
        auth.authenticate(user, response)

        then:
        response.getCookie("username")?.getValue() == usernameCookie
        response.getCookie("userid")?.getValue() == useridCookie
        response.getCookie("user")?.getValue() == userCookie


        where:
        user        | usernameCookie | useridCookie | userCookie
        validUser   | "user1"        | '1'          | '{"username":"user1","role":{"title":"user","bitMask":2}}'
        invalidUser | null           | null         | null
        manager     | "user4"        | '4'          | '{"username":"user4","role":{"title":"manager","bitMask":3}}'


    }

    def "should verify database changes on successful and failure authentications"() {
        given:
        MockHttpServletResponse response = new MockHttpServletResponse()

        when:
        auth.authenticate(user, response)

        then:
        gSql.rows("select * from authtoken").size() == authTokenEntries
        gSql.firstRow("select username,status from userlog") == userlogEntry

        where:
        user        | authTokenEntries | userlogEntry
        validUser   | 1                | [username: 'user1', status: 'success']
        invalidUser | 0                | [username: 'invaliduser', status: 'fail']
        manager     | 1                | [username: 'user4', status: 'success']

    }



}
