package org.cghr.dataSync.client
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.DataSyncService
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DownloadOrganizerAgentSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    DownloadOrganizerAgent downloadOrganizerAgent
    @Shared
    List dataSet

    def setupSpec() {
        dataSet = new MockData().sampleData.get("inbox")


    }

    def setup() {

        DataSyncService mockService = Stub() { getDownloadInfo() >> dataSet }
        DbStore mockDbStore = Stub() {
            saveOrUpdateFromMapList(dataSet, 'inbox') >> {
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?)".toString(), dataSet[0].values() as List)
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?)".toString(), dataSet[1].values() as List)
            }
        }

        downloadOrganizerAgent = new DownloadOrganizerAgent(mockService, mockDbStore)
        dt.clean("inbox")
    }

    def "should save the download Info data got from server to local database"() {

        when:
        downloadOrganizerAgent.run()

        then:
        gSql.rows("select * from inbox") == dataSet
    }
}

