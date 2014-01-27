package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class DataAccessSpec extends Specification {

    DataAccess dataAccess


    @Shared
    Sql gSql
    @Shared
    DbTester dt
    def dataSet

    def setupSpec() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-context.xml")
        gSql = appContext.getBean("gSql")
        dt = appContext.getBean("dt")
    }

    def setup() {

        dataSet = new MockData().sampleData.get("country")
        dataAccess = new DataAccess()
        DbAccess mockDbAccess = Stub() {
            getRowAsJson("country", "id", "1") >> new Gson().toJson(dataSet[0]).toString()
            getRowAsJson("country", "id", "999") >> "{}"
        }
        dataAccess.dbAccess = mockDbAccess
        dt.cleanInsert("country")
    }

    def "should get requested data as json"() {
        expect:
        dataAccess.getDataAsJson("country", "id", "1") == new Gson().toJson(dataSet[0]).toString()
    }

    def "should get an empty json for an invalid request"() {
        expect:
        dataAccess.getDataAsJson("country", "id", "999") == "{}"
    }
}