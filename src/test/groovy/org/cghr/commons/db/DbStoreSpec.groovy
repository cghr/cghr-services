package org.cghr.commons.db
import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DbStoreSpec extends Specification {

    //specific
    @Autowired
    DbStore dbStore
    @Shared
    def dataSet
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
        dataSetUpdate = new MockData().sampleDataUpdate.get("country")
    }

    def setup() {
        dt.clean("country")
    }


    def "verify data insert from a map to database"() {

        when:
        dbStore.saveOrUpdate(dataSet[0], dataStore)

        then:
        gSql.firstRow("SELECT * FROM country WHERE id=?", [1]) == dataSet[0]
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
}

