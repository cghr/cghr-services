package org.cghr.commons.web.controller
import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DataAccessSpec extends Specification {

    DataAccess dataAccess


    @Autowired
    Sql gSql

    @Autowired
    DbTester dt

    @Shared
    def dataSet

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
    }

    def setup() {

        dt.cleanInsert("country")
        dataAccess = new DataAccess()
        DbAccess mockDbAccess = Stub() {
            getRowAsJson("country", "id", "1") >> new Gson().toJson(dataSet[0]).toString()
            getRowAsJson("country", "id", "999") >> "{}"
        }
        dataAccess.dbAccess = mockDbAccess

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