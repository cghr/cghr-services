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

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyContextLoader.class)
class DbStoreSpec extends Specification {

    //specific
    @Autowired
    DbStore dbStore
    @Shared
    def dataSet
    @Shared
    def countryBatchData
    @Shared
    def dataSetUpdate
    @Shared
    def dataStore = 'country'


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt


    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
        countryBatchData = new MockData().sampleData.get("countryBatchData")
        dataSetUpdate = new MockData().sampleDataUpdate.get("country")
    }

    def setup() {
        dt.clean("country")
        dt.clean("datachangelog")
    }


    def "verify data insert from a map to database"() {

        when:
        dbStore.saveOrUpdate(dataSet[0], dataStore)

        then:
        gSql.firstRow("SELECT * FROM country WHERE id=?", [1]) == dataSet[0]
    }

    def "verify batch data insert from a list of maps"() {
        when:
        dbStore.saveOrUpdateBatch(countryBatchData)

        then:
        gSql.rows("select * from country").size() == 3;

    }

    def "verify data insert from a map List to database"() {
        when:
        dbStore.saveOrUpdateFromMapList(dataSet, dataStore)

        then:
        gSql.rows("select * from country") == dataSet
    }

    def "verify data update from a map to database"() {
        when:
        dbStore.saveOrUpdate(dataSet[0], dataStore)
        dbStore.saveOrUpdate(dataSetUpdate[0], dataStore)

        then:
        gSql.firstRow("SELECT * FROM country WHERE id=?", [1]) == dataSetUpdate[0]
    }

    def "verify data update from a map List to database"() {
        when:
        dbStore.saveOrUpdateFromMapList(dataSet, dataStore)
        dbStore.saveOrUpdateFromMapList(dataSetUpdate, dataStore)

        then:
        gSql.rows("select * from country") == dataSetUpdate
    }

    def "should create a datachangelog for a given log"() {

        given:
        Map log = [datastore: dataStore, data: dataSet[0]]
        String expectedLog


        when:
        dbStore.createDataChangeLogs(dataSet[0], dataStore)


        then:
        gSql.firstRow("select count(*) count from datachangelog").count == 1;
        gSql.eachRow("select log from datachangelog") {
            expectedLog = it.log.getAsciiStream().getText();
        }
        expectedLog == new Gson().toJson(log)
    }

    def "should verify data to be new or not"() {

        expect:
        dbStore.isNewData('country', 'id', '1')

        where:
        datastore | key  | value || result
        'country' | 'id' | '1'   || true
        'country' | 'id' | '999' || false


    }
}

