package org.cghr.dataSync.service

import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 20/2/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class AgentServiceSpec extends Specification {

    AgentService agentService

    //General
    @Autowired
    DbTester dt
    @Autowired
    def gSql
    @Shared
    List inboxFiles
    @Shared
    def changelog=[datastore:'country',data:[id:1,name:'india',continent:'asia']]


    def setupSpec() {

        inboxFiles = new MockData().sampleData.get("inbox")
    }


    def setup() {
        DbAccess dbAccess = Stub() {
            getRowsAsListOfMaps("select id,message from inbox where dwnStatus is null", []) >> {
                gSql.rows("select id,message from inbox where dwnStatus is null")
            }
            getRowsAsListOfMaps("select id,message from inbox where distStatus is null", []) >> {
                gSql.rows("select id,message,distList from inbox where dwnStatus is null")
            }
            getRowsAsListOfMaps("select id,message from inbox where impStatus is null",[]) >> {
                gSql.rows("select id,message from inbox where impStatus is null")
            }
        }
        DbStore dbStore = Stub() {
            saveOrUpdateFromMapList(inboxFiles, "inbox") >> {
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?,?)".toString(), inboxFiles[0].values() as List)
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?,?)".toString(), inboxFiles[1].values() as List)

            }
            saveOrUpdate(message:  'file1.json',recepient:'1',"outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '1'])
            }
            saveOrUpdate(message:  'file1.json',recepient:'2',"outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '2'])
            }
            saveOrUpdate(message:  'file2.json',recepient:'3',"outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '3'])
            }
            saveOrUpdate(message:  'file2.json',recepient:'4',"outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '4'])
            }

            saveOrUpdate(changelog.data,changelog.datastore) >> {
                gSql.executeInsert('insert into country(id,name,continent) values(?,?,?)', [1,'india','asia'])
            }
        }
        AwakeFileSession awakeFileSession = Stub() {}



        agentService = new AgentService(dbAccess, dbStore, awakeFileSession)
        dt.cleanInsert("inbox")
    }

    def "should save Download Info to Inbox"() {
        given:
        dt.clean("inbox")

        when:
        agentService.saveDownloadInfo(inboxFiles)

        then:
        gSql.rows("select * from inbox") == inboxFiles


    }

    def "should get inbox files to download"() {

        expect:
        agentService.getInboxFilesToDownload() == [[id: 1, message: 'file1.json'], [id: 2, message: 'file2.json']]


    }

    def "should get downloaded inbox files to distribute"() {

        expect:
        agentService.getInboxFilesToDistribute() == [[id: 1, message: 'file1.json', distList: '1,2'], [id: 2, message: 'file2.json', distList: '3,4']]


    }

    def "should distribute a given message"() {

        given:
        dt.clean("outbox")

        when:
        agentService.distributeMessage("file1.json","1,2")
        agentService.distributeMessage("file2.json","3,4")

        then:
        gSql.rows("select * from outbox where upldStatus is NULL").size()==4
    }

    def "should save data changelog to respective datastore"() {

        given:
        dt.clean("country")


        when:
        agentService.saveLogInfToDatabase(changelog)

        then:
        gSql.rows("select * from country").size()==1

    }

    def "should get files to be imported"() {

        given:
        dt.cleanInsert("inbox")

        expect:
        agentService.getFilesToImport()==[[id: 1, message: 'file1.json'], [id: 2, message: 'file2.json']]


    }

}
