package org.cghr.commons.web.controller
import com.google.gson.Gson
import org.cghr.commons.db.DbAccess
import org.cghr.context.SpringContext
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import spock.lang.Shared
import spock.lang.Specification

class DataAccessBatchSpec extends Specification {

    @Shared
    DataAccessBatch dataAccessBatch
    DbAccess dbAccess=SpringContext.dbAccess
    DbTester dbTester=SpringContext.dbTester

    @Shared
    def dataSet

    def setupSpec() {

        dataSet = new MockData().sampleData.get("country")


    }

    def setup() {

        dbTester.cleanInsert('country')
        dataAccessBatch = new DataAccessBatch(dbAccess)
    }

    def "should get requested data as json"() {
        expect:
        dataAccessBatch.getDataAsJsonArray(table, keyField, keyFieldValue) == result

        where:
        table     | keyField    | keyFieldValue || result
        "country" | "continent" | "asia"        || new Gson().toJson(dataSet).toString()
        "country" | "continent" | "antarctica"  || "[]"
    }
}