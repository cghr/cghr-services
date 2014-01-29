package org.cghr.dataViewModel
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DataModelUtilSpec extends Specification {

    //specific
    DataModelUtil dataModelUtil
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
        DhtmlxGridModelTransformer mockTransformer = Stub() {
            getModel(multipleRowSql, validParamsMultipleRow) >> '{"rows":[{"id":1,"data":[1,"india","asia"]},{"id":2,"data":[2,"pakistan","asia"]},{"id":3,"data":[3,"srilanka","asia"]}]}'
            getModel(multipleRowSql, invalidParamsMultipleRow) >> '{"rows":[]}'
        }

        DbAccess mockDbAccess = Stub() {
            getColumnLabels(multipleRowSql, _) >> 'id,name,continent'
        }
        dataModelUtil = new DataModelUtil(mockTransformer, mockDbAccess)

        dt.cleanInsert("country")
    }


    def "should construct Json Response in required format"() {

        expect:
        json == dataModelUtil.constructJsonResponse(multipleRowSql, validParamsMultipleRow, "#text_filter,#text_filter,#text_filter", "int,str,str")
        emptyDataJson == dataModelUtil.constructJsonResponse(multipleRowSql, invalidParamsMultipleRow, "#text_filter,#text_filter,#text_filter", "int,str,str")

        where:
        json = '{"headings":"id,name,continent",' +
                '"filters":"#text_filter,#text_filter,#text_filter",' +
                '"sortings":"int,str,str",' +
                '"data":{"rows":[{"id":1,"data":[1,"india","asia"]},{"id":2,"data":[2,"pakistan","asia"]},{"id":3,"data":[3,"srilanka","asia"]}]}}'
        emptyDataJson = '{"headings":"id,name,continent",' +
                '"filters":"#text_filter,#text_filter,#text_filter",' +
                '"sortings":"int,str,str",' +
                '"data":{"rows":[]}}'
    }
}
