package org.cghr.dataViewModel

import org.cghr.commons.db.DbAccess
import org.cghr.test.db.MockData
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
    @Shared
    def multipleRowSql = 'select * from country where continent=?'
    @Shared
    def validParamsMultipleRow = ['asia'];
    @Shared
    def invalidParamsMultipleRow = ['dummyContinent'];

    //General
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

    }


    def "should construct Json Response in required format"() {

        expect:
        dataModelUtil.constructJsonResponse(sql, params, filters, sortings) == result

        where:

        sql            | params                   | filters                                  | sortings      || result
        multipleRowSql | validParamsMultipleRow   | "#text_filter,#text_filter,#text_filter" | "int,str,str" || '{"headings":"id,name,continent",' +
                '"filters":"#text_filter,#text_filter,#text_filter",' +
                '"sortings":"int,str,str",' +
                '"data":{"rows":[{"id":1,"data":[1,"india","asia"]},{"id":2,"data":[2,"pakistan","asia"]},{"id":3,"data":[3,"srilanka","asia"]}]}}'

        multipleRowSql | invalidParamsMultipleRow | "#text_filter,#text_filter,#text_filter" | "int,str,str" || '{"headings":"id,name,continent",' +
                '"filters":"#text_filter,#text_filter,#text_filter",' +
                '"sortings":"int,str,str",' +
                '"data":{"rows":[]}}'

    }
}
