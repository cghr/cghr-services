package org.cghr.dataSync.client

import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.DataSyncService
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class DownloadOrganizerAgentSpec extends Specification {

    @Shared
    Sql gSql
    @Shared
    DbTester dt

    DownloadOrganizerAgent downloadOrganizerAgent
    @Shared
    List dataSet

    def setupSpec() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-context.xml")
        gSql = appContext.getBean("gSql")
        dt = appContext.getBean("dt")
        dataSet = new MockData().sampleData.get("inbox")


    }

    def setup() {

        DataSyncService mockService = Stub() { getDownloadInfo() >> dataSet }
        DbStore mockDbStore = Stub() {
            saveOrUpdateFromMapList(dataSet, 'inbox') >> {
                gSql.executeInsert("insert into inbox(id,message,sender,distList) values(?,?,?,?)".toString(), dataSet[0].values() as List)
                gSql.executeInsert("insert into inbox(id,message,sender,distList) values(?,?,?,?)".toString(), dataSet[1].values() as List)
            }
        }

        downloadOrganizerAgent = new DownloadOrganizerAgent(mockService, mockDbStore)
        dt.clean("inbox")
    }

    def "should save the download Info data got from server to local database"() {

        when:
        downloadOrganizerAgent.run()

        then:
        gSql.rows("select id,message,sender,distList from inbox") == dataSet
    }
}

