package org.cghr.commons.db
import com.google.gson.Gson
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DbAccessSpec extends Specification {

    //specific
    @Autowired
    DbAccess dbAccess //SUS
    def singleRowSql = 'select * from country where name=?'
    def validParamsSingleRow = ['india'];
    def invalidParamsSingleRow = ['dummyContry'];

    def multipleRowSql = 'select * from country where continent=?'
    def validParamsMultipleRow = ['asia'];
    def invalidParamsMultipleRow = ['dummyContinent'];


    @Shared
    def dataSet
    //@Shared
    @Autowired
    def gSql
    def dataStore = 'country'

    //General
    @Autowired
    DbTester dt

    def setupSpec() {

        dataSet = new MockData().sampleData.get("country")


    }
    def setup()
    {
        dt.cleanInsert("country")
    }


    def "should have rows for a valid sql and no rows for an invalid sql"() {

        expect:
        dbAccess.hasRows(singleRowSql, validParamsSingleRow) == true
        dbAccess.hasRows(singleRowSql, invalidParamsSingleRow) == false


    }

    def "should get database row as a Map object"() {

        expect:
        assert rowMap.equals(dbAccess.getRowAsMap(singleRowSql, validParamsSingleRow))
        assert emptyMap.equals(dbAccess.getRowAsMap(singleRowSql, invalidParamsSingleRow))

        where:
        rowMap = dataSet[0]
        emptyMap = [:]
    }


    def "should get db rows as List of Map Objects"() {
        expect:
        list == dbAccess.getRowsAsListOfMaps(multipleRowSql, validParamsMultipleRow)
        emptyList == dbAccess.getRowsAsListOfMaps(multipleRowSql, invalidParamsMultipleRow)

        where:
        list = dataSet
        emptyList = []
    }


    def "should get db row as a Json"() {

        expect:
        jsonString == dbAccess.getRowAsJson(singleRowSql, validParamsSingleRow)
        emptyJson == dbAccess.getRowAsJson(singleRowSql, invalidParamsSingleRow)

        where:
        //jsonString='{"id":1,"name":"india","continent":"asia"}'
        jsonString = new Gson().toJson(dataSet[0]).toString()
        emptyJson = '{}'
    }

    def "should get db row as Json from dataStore,key,value"() {

        expect:
        jsonString == dbAccess.getRowAsJson(dataStore, 'id', '1')
        emptyJson == dbAccess.getRowAsJson(dataStore, 'id', '0')

        where:
        //jsonString='{"id":1,"name":"india","continent":"asia"}'
        jsonString = new Gson().toJson(dataSet[0]).toString()
        emptyJson = '{}'

    }

    def "should get db rows as JsonArray of Json Objects"() {

        expect:
        jsonArray == dbAccess.getRowsAsJsonArray(multipleRowSql, validParamsMultipleRow)
        emptyArray == dbAccess.getRowsAsJsonArray(multipleRowSql, invalidParamsMultipleRow)

        where:
        jsonArray = new Gson().toJson(dataSet)
        emptyArray = '[]'
    }

    def "should get db rows as JsonArray of Json Objects With Only Values No Column Names (keys)"() {

        expect:
        jsonArray == dbAccess.getRowsAsJsonArrayOnlyValues(multipleRowSql, validParamsMultipleRow)
        emptyArray == dbAccess.getRowsAsJsonArrayOnlyValues(multipleRowSql, invalidParamsMultipleRow)

        where:
        jsonArray = '[[1,"india","asia"],[2,"pakistan","asia"],[3,"srilanka","asia"]]'
        emptyArray = '[]'
    }

    def "should get  column Labels for an sql"() {
        expect:
        labels == dbAccess.getColumnLabels(singleRowSql, validParamsSingleRow)
        where:
        labels = 'id,name,continent'
    }

    def "should remove data for a given table with a given condition"() {

        when:
        dbAccess.removeData("country", "continent", "asia")

        then:
        gSql.rows("select * from country where continent=?", ['asia']).size() == 0

    }
}
