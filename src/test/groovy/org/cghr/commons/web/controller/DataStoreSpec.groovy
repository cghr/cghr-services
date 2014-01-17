package org.cghr.commons.web.controller

import groovy.sql.Sql

import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import spock.lang.Shared
import spock.lang.Specification

class DataStoreSpec extends Specification {

	DataStore dataStore
	def data=[id:1,name:'india',continent:'asia']


	def dataSet
	@Shared Sql gSql
	@Shared DbTester dt
	def setupSpec() {
		ApplicationContext appContext=new ClassPathXmlApplicationContext("spring-context.xml")
		gSql=appContext.getBean("gSql")
		dt=appContext.getBean("dt")
	}
	def setup() {

		dataSet=new MockData().sampleData.get("country")
		DbStore mockDbStore=Mock()
		dataStore=new DataStore()
		dataStore.dbStore=mockDbStore
		mockDbStore.saveOrUpdate(dataSet[0], "country") >> {gSql.executeInsert("insert into country(id,name,continent) values(?,?,?)",[1, "india", "asia"])}

		dt.clean("country")
	}
	def "should save a map to database"() {
		setup:
		Map data=dataSet[0]
		data.put("datastore","country")

		when:
		dataStore.saveData(data)


		then:
		gSql.firstRow("select * from country where id=?",[1])==dataSet[0]
	}
}