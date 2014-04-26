package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DataStoreBatchSpec extends Specification {

    @Shared
    DataStoreBatch dataStoreBatch
    @Shared
    def dataChangelogs
    def data = [id: 1, name: 'india', continent: 'asia']
    @Autowired
    DbStore dbStore
    @Shared
    def countryBatchData


    @Shared
    def dataSet
    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
        countryBatchData = new MockData().sampleData.get("countryBatchData")
    }

    def setup() {


        dataStoreBatch = new DataStoreBatch(dbStore)
        dt.clean("country")
    }

    def "should save a map to database"() {
        setup:
        String changelogs = new Gson().toJson(countryBatchData)

        when:
        dataStoreBatch.saveData(changelogs)


        then:
        gSql.rows("select * from country").size() == 3
    }
}
