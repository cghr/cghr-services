package org.cghr.security.controller

import groovy.sql.Sql

import javax.servlet.http.HttpServletResponse

import org.cghr.security.model.User
import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse

import spock.lang.Shared
import spock.lang.Specification




class AuthSpec extends Specification {


	@Shared Sql gSql
	@Shared DbTester dt
	Auth auth
	User validUser=new User(username:'user1',password:'secret1')
	User invalidUser=new User(username:'invaliduser',password:'invalidpassword')
	User manager=new User(username:'user4',password:'secret4')


	def setupSpec() {

		ApplicationContext appContext=new ClassPathXmlApplicationContext("spring-context.xml")
		gSql=appContext.getBean("gSql")
		dt=appContext.getBean("dt")
	}

	def setup() {

		//Mocking User Service
		UserService mockUserService=Mock()
		def authtoken="ABCDEDGH-12345"

		mockUserService.isValid(validUser) >> true
		mockUserService.isValid(invalidUser) >> false
		mockUserService.isValid(manager) >> true


		mockUserService.getUserCookieJson(validUser) >> '{"username":"user1","role":{"title":"user","bitMask":2}}'
		mockUserService.getUserCookieJson(invalidUser) >> '{}'
		mockUserService.getUserCookieJson(manager) >> '{"username":"user4","role":{"title":"manager","bitMask":3}}'

		mockUserService.getId(validUser) >> "1"
		mockUserService.getId(validUser) >> null
		mockUserService.getId(manager) >> "4"

		mockUserService.logUserAuthStatus(validUser,"success") >> {
			gSql.executeInsert("insert into userlog(username,status) values(?,?)",[
				validUser.username,
				"success"
			]) }
		mockUserService.logUserAuthStatus(invalidUser,"fail") >> { gSql.executeInsert("insert into userlog(username,status) values(?,?)",[invalidUser.username, "fail"]) }
		mockUserService.logUserAuthStatus(manager,"success") >> { gSql.executeInsert("insert into userlog(username,status) values(?,?)",[manager.username, "success"]) }

		mockUserService.getUserJson(validUser) >> '{"id":1,"username":"user1","password":"secret1","role":"user","status":"active"}'
		mockUserService.getUserJson(invalidUser) >> '{}'
		mockUserService.getUserJson(manager) >> '{"id":4,"username":"user4","password":"secret4","role":"manager","status":"active"}'
		mockUserService.saveAuthToken(_, validUser) >> {
			gSql.executeInsert("insert into authtoken(token,username,role) values(?,?,?)",[
				authtoken,
				validUser.username,
				'user'
			]) }

		mockUserService.saveAuthToken(_, manager) >> {
			gSql.executeInsert("insert into authtoken(username,token,role) values(?,?,?)",[
				authtoken,
				manager.username,
				'manager'
			]) }

		auth=new Auth(mockUserService)


		dt.cleanInsert("user")
		dt.clean("authtoken")
		dt.clean("userlog")

	}


	def "should authenticate a valid user"() {
		given:
		HttpServletResponse response=new MockHttpServletResponse()

		when:
		def jsonResp=auth.authenticate(validUser,response)

		then:
		response.status==HttpStatus.OK.value
		jsonResp=='{"id":1,"username":"user1","password":"secret1","role":"user","status":"active"}'
		response.getCookie("authtoken")!=null
		response.getCookie("username").getValue()=='user1'
		response.getCookie("userid").getValue()=='1'
		response.getCookie("user").getValue()=='{"username":"user1","role":{"title":"user","bitMask":2}}'
		gSql.rows("select * from authtoken").size()==1                      //check whether authtoken created in database
		gSql.firstRow("select username,status from userlog")==[username:'user1',status:'success']  //check for userlog
	}
	def "should reject an invalid user"() {
		given:
		HttpServletResponse response=new MockHttpServletResponse()

		when:
		def jsonResp=auth.authenticate(invalidUser,response)

		then:
		response.status==HttpStatus.FORBIDDEN.value
		jsonResp=="{}"
		response.getCookie("username")==null
		response.getCookie("userid")==null
		response.getCookie("user")==null
		gSql.rows("select * from authtoken").size()==0
		gSql.firstRow("select username,status from userlog")==[username:'invaliduser',status:'fail'] //check for userlog
	}
	def "should authenticate a valid user with role manager"() {
		given:
		HttpServletResponse response=new MockHttpServletResponse()

		when:
		def jsonResp=auth.authenticate(manager,response)



		then:
		response.status==HttpStatus.OK.value
		jsonResp=='{"id":4,"username":"user4","password":"secret4","role":"manager","status":"active"}'
		response.getCookie("username").getValue()=='user4'
		response.getCookie("userid").getValue()=='4'
		response.getCookie("user").getValue()=='{"username":"user4","role":{"title":"manager","bitMask":3}}'
		gSql.rows("select * from authtoken").size()==1                      //check whether authtoken created in database
		gSql.firstRow("select username,status from userlog")==[username:'user4',status:'success']  //check for userlog
	}

}
