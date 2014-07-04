package org.cghr.dataSync.service

import com.google.gson.Gson
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.GenericGroovyContextLoader
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/4/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class AgentServiceSpec extends Specification {

    //General
    @Autowired
    DbTester dt
    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore
    @Autowired
    def gSql

    @Shared
    List inboxMessages
    @Shared
    def changelog = [datastore: 'country', data: [id: 1, name: 'india', continent: 'asia']]
    @Shared
    List countryData
    @Shared
    List dataChangeLogs
    @Shared
    Gson gson = new Gson()
    AgentService agentService
    def downloadInfo


    def setupSpec() {


        inboxMessages = new MockData().sampleData.get("inbox")
        countryData = new MockData().sampleData.get("country")
        dataChangeLogs = new MockData().sampleData.get("datachangelog")
    }


    def setup() {

        dt.clean('inbox')


        String dwnldInfoUrl = "http://dummyServer/downloadInfo"
        String upldUrl = "http://dummyServer/dataStoreBatch"
        String dataBatchUrl = "http://dummyServer/dataAccessBatch/"
        RestTemplate restTemplate = Stub() {
            getForObject(dwnldInfoUrl, List.class) >> inboxMessages
            getForObject(dataBatchUrl + "country/continent/asia", List.class) >> countryData

        }
        Integer changelogChunk = 20
        AwakeFileSession awakeFileSession = Stub() {

        }
        Map fileStoreFactory = Stub() {

        }
        String userHome = ''
        AgentDownloadService agentDownloadService = new AgentDownloadService(dbAccess, dbStore, dwnldInfoUrl, dataBatchUrl, restTemplate)
        AgentUploadService agentUploadService = new AgentUploadService(dbAccess, dbStore, upldUrl, restTemplate, changelogChunk)
        AgentMsgDistService agentMsgDistService = new AgentMsgDistService(dbStore, dbAccess)
        AgentFileUploadservice agentFileUploadservice = new AgentFileUploadservice(dbAccess, dbStore, awakeFileSession, fileStoreFactory)
        agentService = new AgentService(agentDownloadService, agentUploadService, agentMsgDistService, agentFileUploadservice)
    }


    def "should get download Info from the Sync server"() {
        expect:
        agentService.getDownloadInfo() == inboxMessages

    }

    def "should save download info to inbox"() {
        when:
        agentService.saveDownloadInfo(inboxMessages)

        then:
        gSql.rows("select * from inbox").size() == 2


    }

    def "should get Inbox messages to download"() {

        given:
        dt.cleanInsert("inbox")

        expect:
        agentService.getInboxMessagesToDownload() == gSql.rows("select * from inbox where impStatus is null")

    }

    def "should download and import a message"() {

        given:
        dt.clean('country')
        Map message = [datastore: 'country', ref: 'continent', refId: 'asia'];

        when:
        agentService.downloadAndImport(message)
        agentService.importSuccessful(message)

        then:
        gSql.rows('select * from country').size() == 3
        gSql.rows('select * from inbox where impStatus is null').size() == 0

    }

    def "should get all inbox messages to distribute"() {
        given:
        dt.cleanInsert('inbox')
        gSql.execute('update inbox set impStatus=1')

        expect:
        agentService.getInboxMessagesToDistribute() == gSql.rows('select * from inbox where impStatus is not null and distStatus is null')
    }


    def "should distribute a message to a recipient"() {
        given:
        Map message = [datastore: 'country', ref: 'continent', refId: 'asia'];
        String recipient = '15'
        dt.clean('outbox')

        when:
        agentService.distributeMessage(message, recipient)

        then:
        gSql.firstRow("select datastore,ref,refId,recipient from outbox") == [datastore: 'country', ref: 'continent', refId: 'asia', recipient: '15'];

    }

    def "should update the status of distributed message"() {
        given:
        Map message = [id: 1, datastore: 'country', ref: 'continent', refId: 'asia'];
        dt.cleanInsert('inbox')

        when:
        agentService.distributeSuccessful(message)

        then:
        gSql.firstRow('select distStatus from inbox where id=1').distStatus == '1'

    }

    def "should get datachangelog chunks"() {
        given:
        dt.cleanInsert('datachangelog')

        expect:
        agentService.getDataChangelogChunks() == 1

    }

    def "should get datachangelog batch"() {
        given:
        dt.cleanInsert('datachangelog')
        List list = []
        gSql.eachRow("select log from datachangelog") {

            list << it.log.getAsciiStream().getText()
        }

        expect:
        agentService.getDataChangelogBatch() == list.toString()

    }


    def "should update the status of a succesfully posted batc of changelogs"() {
        given:
        dt.cleanInsert('datachangelog')

        when:
        agentService.postBatchSuccessful()

        then:
        gSql.rows('select * from datachangelog where status=1').size() == 3


    }

    def "should get all the file changelogs"() {
        setup:
        dt.cleanInsert('filechangelog')
        def filechangelogs = new MockData().sampleData.get('filechangelog')

        expect:
        agentService.getFileChangelogs() == filechangelogs
    }

    def "should change the status of the upload"() {
        given:
        dt.cleanInsert('filechangelog')

        when:
        agentService.fileUploadSuccessful(1)
        println gSql.rows('select * from filechangelog')

        then:
        gSql.firstRow('select status from filechangelog where id=?', ['1']).status == '1'

    }


}