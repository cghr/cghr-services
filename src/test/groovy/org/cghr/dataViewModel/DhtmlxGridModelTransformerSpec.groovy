package org.cghr.dataViewModel
import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DhtmlxGridModelTransformerSpec extends Specification {

    //specific
    @Autowired
    DhtmlxGridModelTransformer transformer

    @Shared
    def dataSet
    def dataStore = 'country'
    def multipleRowSql = 'select * from country where continent=?'
    def validParamsMultipleRow = ['asia'];
    def invalidParamsMultipleRow = ['dummyContinent'];

    //General
    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    def setupSpec() {


        dataSet = new MockData().sampleData.get("country")

    }

    def setup() {

        dt.cleanInsert("country")
    }

    def "verify dhtmlxGrid transformer"() {

        expect:
        model == transformer.getModel(multipleRowSql, validParamsMultipleRow)
        emptyModel == transformer.getModel(multipleRowSql, invalidParamsMultipleRow)

        where:
        model = '{"rows":[{"id":1,"data":[1,"india","asia"]},{"id":2,"data":[2,"pakistan","asia"]},{"id":3,"data":[3,"srilanka","asia"]}]}'
        emptyModel = '{"rows":[]}'
    }
}