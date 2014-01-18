package org.cghr.security.controller

import groovy.sql.Sql

import javax.servlet.FilterChain

import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import spock.lang.Shared
import spock.lang.Specification

class AuthFilterSpec extends Specification {

	AuthFilter filter
	def request=new MockHttpServletRequest()
	def response=new MockHttpServletResponse()
	FilterChain chain=Mock()


	@Shared Sql gSql
	@Shared DbTester dt
	def authtoken="ABCDEFG-12345"
	def setupSpec() {
		ApplicationContext appContext=new ClassPathXmlApplicationContext("spring-context.xml")
		gSql=appContext.getBean("gSql")
		dt=appContext.getBean("dt")
	}
	def setup() {

		RequestParser mockParser=Stub(){
			getAuthTokenFromCookies(request) >>> [authtoken, null]
		}

		UserService mockUserService=Stub(){
			isValidToken(authtoken) >>> [true, false]
		}
		filter=new AuthFilter(mockUserService,mockParser)
		dt.clean("authtoken")
	}
	def "should authorise valid User"() {
		given:
		def response1=new MockHttpServletResponse() //success case
		def response2=new MockHttpServletResponse() //fail case

		when:
		filter.doFilter(request,response1 ,chain)
		filter.doFilter(request,response2 ,chain)

		then:
		response1.status==HttpStatus.OK.value
		response2.status==HttpStatus.UNAUTHORIZED.value
		1 * chain.doFilter(request,response1 )

	}
}