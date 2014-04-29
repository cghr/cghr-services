package org.cghr.commons.web.controller
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.context.SpringContext
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification

class DataStoreSpec extends Specification {

    @Shared DataStore dataStore
    def data = [id: 1, name: 'india', continent: 'asia']


    @Shared
    def dataSet
    Sql gSql=SpringContext.sql
    DbStore dbStore=SpringContext.dbStore
    DbTester dt=SpringContext.dbTester

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
    }

    def setup() {


        dataStore = new DataStore(dbStore)

        dt.clean("country")
        dt.clean("datachangelog")
    }

    def "should save a map to database"() {
        setup:
        Map data = dataSet[0]
        data.put("datastore", "country")

        when:
        dataStore.saveData(data)


        then:
        gSql.firstRow("select * from country where id=?", [1]) == dataSet[0]
        gSql.rows("select * from datachangelog").size()==1
    }
}
