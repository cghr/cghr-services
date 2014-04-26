package org.cghr.commons.web.controller
import com.google.gson.Gson
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.MockData
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DataAccessSpec extends Specification {

    @Shared
    DataAccess dataAccess

    @Shared
    def dataSet

    def setupSpec() {

        dataSet = new MockData().sampleData.get("country")
        DbAccess mockDbAccess = Stub() {
            getRowAsJson("country", "id", "1") >> new Gson().toJson(dataSet[0]).toString()
            getRowAsJson("country", "id", "999") >> "{}"
        }
        dataAccess=new DataAccess(mockDbAccess)

    }

    def "should get requested data as json"() {
        expect:
        dataAccess.getDataAsJson(table, id,value) == result

        where:
        table     | id   | value || result
        "country" | "id" | "1"    | new Gson().toJson(dataSet[0]).toString()
        "country" | "id" | "999"  | "{}"
    }
}