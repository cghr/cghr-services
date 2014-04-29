package org.cghr.dataViewModel
import groovy.sql.Sql
import org.cghr.context.SpringContext
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification

class DhtmlxGridModelTransformerSpec extends Specification {

    //specific
    DhtmlxGridModelTransformer transformer

    @Shared
    def dataSet
    @Shared
    def dataStore = 'country'
    @Shared
    def multipleRowSql = 'select * from country where continent=?'
    @Shared
    def validParamsMultipleRow = ['asia'];
    @Shared
    def invalidParamsMultipleRow = ['dummyContinent'];

    //General
    @Autowired
    Sql gSql=SpringContext.sql
    @Autowired
    DbTester dt=SpringContext.dbTester

    def setupSpec() {


        dataSet = new MockData().sampleData.get("country")

    }

    def setup() {
        transformer = new DhtmlxGridModelTransformer(gSql)
        dt.cleanInsert("country")
    }

    def "verify dhtmlxGrid transformer"() {

        expect:
        transformer.getModel(sql, params) == result

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || '{"rows":[{"id":1,"data":[1,"india","asia"]},{"id":2,"data":[2,"pakistan","asia"]},{"id":3,"data":[3,"srilanka","asia"]}]}'
        multipleRowSql | invalidParamsMultipleRow || '{"rows":[]}'
    }
}