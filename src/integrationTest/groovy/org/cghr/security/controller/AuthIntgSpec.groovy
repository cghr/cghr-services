package org.cghr.security.controller
import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.security.model.User
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/1/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class AuthIntegrationSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    @Autowired
    Auth auth
    @Shared
    List dataSet
    @Shared
    User validUser = new User(username: 'user1', password: 'secret1')
    @Shared
    User invalidUser = new User(username: 'invaliduser', password: 'secret1')


    def setupSpec() {

        dataSet = new MockData().sampleData.get("user")
    }


    def setup() {

        dt.cleanInsert("user")
        dt.clean('authtoken,userlog')

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

    }


}
