package org.cghr.commons.db
import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
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



    def "should get database row as a Map object"() {

        expect:
        dbAccess.firstRow(sql, params) == result

        where:
        sql          | params                 || result
        singleRowSql | validParamsSingleRow   || dataSet[0]
        singleRowSql | invalidParamsSingleRow || [:]
    }

    def "should get database row as a Map object without params"() {

        expect:
        dbAccess.firstRow(sql) == result

        where:
        sql                                        || result
        "select * from country where name='india'" || dataSet[0]
        "select * from country where name='dummy'" || [:]
    }

    def "should get db rows as List of Map Objects"() {
        expect:
        dbAccess.rows(sql, params) == result

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || dataSet
        multipleRowSql | invalidParamsMultipleRow || []


    }

    def "should get db rows as List of Map Objects without params"() {
        expect:
        dbAccess.rows(sql) == result

        where:
        sql                                             || result
        "select * from country where continent='asia'"  || dataSet
        "select * from country where continent='dummy'" || []


    }


    def "should get  column Labels for an sql"() {

        expect:
        dbAccess.columns(sql, params).join(',') == result

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


}
