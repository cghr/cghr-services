package org.cghr.commons.db

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class DbAccessSpec extends Specification {

    //specific

    @Autowired
    DbAccess dbAccess

    @Shared
    def singleRowSql = 'select * from country where name=?'
    @Shared
    def singleRowSqlWithoutParams = "select * from country where name='india'"
    @Shared
    def validParamsSingleRow = ['india'];
    @Shared
    def invalidParamsSingleRow = ['dummyContry'];

    @Shared
    def multipleRowSql = 'select * from country where continent=?'
    @Shared
    def validParamsMultipleRow = ['asia'];
    @Shared
    def invalidParamsMultipleRow = ['dummyContinent'];


    @Shared
    def dataSet


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    def dataStore = 'country'


    def setup() {
        dt.cleanInsert("country")
    }

    def setupSpec() {

        dataSet = new MockData().sampleData.get("country")

    }


    def "should have rows for a valid sql and no rows for an invalid sql"() {


        expect:
        dbAccess.hasRows(sql, params) == result


        where:
        sql          | params                 || result
        singleRowSql | validParamsSingleRow   || true
        singleRowSql | invalidParamsSingleRow || false

    }

    def "should have rows for a valid sql and no rows for an invalid sql without params"() {

        expect:
        dbAccess.hasRows(sql) == result

        where:
        sql                                        || result
        "select * from country where name='india'" || true
        "select * from country where name='dummy'" || false

    }

    def "should get database row as a Map object"() {

        expect:
        dbAccess.getRowAsMap(sql, params) == result

        where:
        sql          | params                 || result
        singleRowSql | validParamsSingleRow   || dataSet[0]
        singleRowSql | invalidParamsSingleRow || [:]
    }

    def "should get database row as a Map object without params"() {

        expect:
        dbAccess.getRowAsMap(sql) == result

        where:
        sql                                        || result
        "select * from country where name='india'" || dataSet[0]
        "select * from country where name='dummy'" || [:]
    }

    def "should get db rows as List of Map Objects"() {
        expect:
        dbAccess.getRowsAsListOfMaps(sql, params) == result

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || dataSet
        multipleRowSql | invalidParamsMultipleRow || []


    }

    def "should get db rows as List of Map Objects without params"() {
        expect:
        dbAccess.getRowsAsListOfMaps(sql) == result

        where:
        sql                                             || result
        "select * from country where continent='asia'"  || dataSet
        "select * from country where continent='dummy'" || []


    }


    def "should get db row as a Json"() {

        expect:
        dbAccess.getRowAsJson(sql, params) == result

        where:
        sql          | params                 || result
        singleRowSql | validParamsSingleRow   || new Gson().toJson(dataSet[0]).toString()
        singleRowSql | invalidParamsSingleRow || '{}'

    }

    def "should get db row as a Json without params"() {

        expect:
        dbAccess.getRowAsJson(sql) == result

        where:
        sql                                        || result
        "select * from country where name='india'" || new Gson().toJson(dataSet[0]).toString()
        "select * from country where name='dummy'" || '{}'

    }

    def "should get db row as Json from dataStore,key,value"() {


        expect:
        dbAccess.getRowAsJson(dataStore, key, value) == result

        where:
        key  | value || result
        'id' | '1'   || new Gson().toJson(dataSet[0]).toString()
        'id' | '0'   || '{}'


    }

    def "should get db rows as JsonArray of Json Objects"() {

        expect:
        dbAccess.getRowsAsJsonArray(sql, params) == result

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || new Gson().toJson(dataSet)
        multipleRowSql | invalidParamsMultipleRow || '[]'

    }

    def "should get db rows as JsonArray of Json Objects With Only Values No Column Names (keys)"() {

        expect:
        dbAccess.getRowsAsJsonArrayOnlyValues(sql, params) == result

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || '[[1,"india","asia"],[2,"pakistan","asia"],[3,"srilanka","asia"]]'
        multipleRowSql | invalidParamsMultipleRow || '[]'

    }

    def "should get  column Labels for an sql"() {

        expect:
        dbAccess.getColumnLabels(sql, params) == result

        where:
        sql          | params               || result
        singleRowSql | validParamsSingleRow || 'id,name,continent'

    }

    def "should remove data for a given table with a given condition"() {

        when:
        dbAccess.removeData("country", "continent", "asia")


        then:
        gSql.rows(sql, params).size() == result

        where:
        sql                                       | params   || result
        'select * from country where continent=?' | ['asia'] || 0


    }

    def "should process the closure for each row"() {

        given:
        List result = []
        Closure closure = {
            row ->
                result << row.name
        }


        when:
        dbAccess.eachRow(multipleRowSql, validParamsMultipleRow, result, closure)

        then:
        result == ['india', 'pakistan', 'srilanka']


    }
}
