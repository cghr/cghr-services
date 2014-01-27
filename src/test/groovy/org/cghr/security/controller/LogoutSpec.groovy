package org.cghr.security.controller

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.security.model.User
import org.cghr.test.db.DbTester
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.Cookie

class LogoutSpec extends Specification {

    Logout logout

    @Shared
    Sql gSql
    @Shared
    DbTester dt
    User validUser = new User(username: 'user1', password: 'secret1')
    User invalidUser = new User(username: 'invaliduser', password: 'invalidpassword')
    def authtoken = 'ABCDEFG-12345'

    def setupSpec() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-context.xml")
        gSql = appContext.getBean("gSql")
        dt = appContext.getBean("dt")
    }

    def setup() {

        DbAccess mockDbAccess = Stub() {
            removeData("authtoken", "token", authtoken) >> {
                def sql = "delete from authtoken where token=?".toString()
                gSql.executeUpdate(sql, [authtoken])
            }
        }

        logout = new Logout(mockDbAccess)
        dt.clean("authtoken")
    }


    def "should invalidate an user session"() {
        given:
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)", [
                authtoken,
                validUser.username,
                'user'
        ])

        Cookie authTokenCookie = new Cookie("authtoken", authtoken)
        Cookie[] cookies = [authTokenCookie]
        request.setCookies(cookies)


        when:
        logout.invalidateSession(request, response)

        then:
        response.getCookie("authtoken").getValue() == null
        response.getCookie("user").getValue() == null
        gSql.rows("select * from authtoken").size() == 0
    }
}
