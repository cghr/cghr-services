package org.cghr.commons.web.controller

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
        dt.clean('datachangelog')
    }

    def "should save a map to database"() {
        setup:
        //String changelogs = new Gson().toJson(countryBatchData)
        Map[] array=[countryBatchData[0],countryBatchData[1],countryBatchData[2]]

        when:
        dataStoreBatch.saveData(array)


        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog").size()==3
    }
}
