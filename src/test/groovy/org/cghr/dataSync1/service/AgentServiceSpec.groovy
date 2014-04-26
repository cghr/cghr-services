package org.cghr.dataSync1.service
import com.google.gson.Gson
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync1.util.FileManager
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 20/2/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class AgentServiceSpec extends Specification {

    AgentService agentService
    String outboxPath = File.createTempDir().absolutePath + '/'
    String inboxPath = File.createTempDir().absolutePath + '/'

    //General
    @Autowired
    DbTester dt
    @Autowired
    def gSql
    @Shared
    List inboxFiles
    @Shared
    def changelog = [datastore: 'country', data: [id: 1, name: 'india', continent: 'asia']]
    @Shared
    List countryData
    @Shared
    List dataChangeLogs
    @Shared
    Gson gson = new Gson()
    @Shared
    String outboxFile = 'file1.json'
    @Shared
    String inboxFile = 'file1.json'
    @Shared
    File inboxFileObject = new File(inboxPath + inboxFile)


    def setupSpec() {

        inboxFiles = new MockData().sampleData.get("inbox")
        countryData = new MockData().sampleData.get("country")
        dataChangeLogs = new MockData().sampleData.get("datachangelog")
    }


    def setup() {
        DbAccess dbAccess = Stub() {
            getRowsAsListOfMaps("select id,message from inbox where dwnStatus is null", []) >> {
                gSql.rows("select id,message from inbox where dwnStatus is null")
            }
            getRowsAsListOfMaps("select id,message from inbox where distStatus is null", []) >> {
                gSql.rows("select id,message,distList from inbox where dwnStatus is null")
            }
            getRowsAsListOfMaps("select id,message from inbox where impStatus is null", []) >> {
                gSql.rows("select id,message from inbox where impStatus is null")
            }
            eachRow("select log from datachangelog where status is null", [], [], _) >> {
                countryData
            }
            getRowsAsListOfMaps("select id,message from outbox where upldStatus is null", []) >> {
                gSql.rows("select id,message from outbox where upldStatus is null", [])
            }

        }




        DbStore dbStore = Stub() {
            saveOrUpdateFromMapList(inboxFiles, "inbox") >> {
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?,?)".toString(), inboxFiles[0].values() as List)
                gSql.executeInsert("insert into inbox values(?,?,?,?,?,?,?)".toString(), inboxFiles[1].values() as List)

            }
            saveOrUpdate(message: 'file1.json', recepient: '1', "outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '1'])
            }
            saveOrUpdate(message: 'file1.json', recepient: '2', "outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '2'])
            }
            saveOrUpdate(message: 'file2.json', recepient: '3', "outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '3'])
            }
            saveOrUpdate(message: 'file2.json', recepient: '4', "outbox") >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1.json', '4'])
            }

            saveOrUpdate(changelog.data, changelog.datastore) >> {
                gSql.executeInsert('insert into country(id,name,continent) values(?,?,?)', [1, 'india', 'asia'])
            }
            saveOrUpdate([message: outboxFile], 'outbox') >> {
                gSql.executeInsert('insert into outbox(message) values(?)', [outboxFile])
            }
            saveOrUpdate([id: '1', upldStatus: 1], "outbox") >> {
                gSql.execute('update outbox set upldStatus=? where id=?', ['1', '1'])
            }
            saveOrUpdate([id: '2', upldStatus: 1], "outbox") >> {
                gSql.execute('update outbox set upldStatus=? where id=?', ['1', '2'])
            }
            saveOrUpdate([dwnStatus:'1',message: inboxFile],"inbox") >> {

                gSql.execute('update inbox set dwnStatus=1 where message=?',[inboxFile])
            }
        }
        String serverUploadDir = ""
        AwakeFileSession awakeFileSession = Stub() {

            download('file1.json', _) >> {

                println 'tmp file downloaded'
                File file = new File(inboxPath + inboxFile)
                file.write(gson.toJson(countryData))
            }

            File file = new File(outboxPath + outboxFile)
            upload(_, _) >> {

                println 'upload stub,outbox file ' + outboxFile
                gSql.execute("update outbox set upldStatus=1 where message=?", [outboxFile])
                println gSql.rows("select * from outbox where message=? and upldStatus=1", [outboxFile])

            }
        }
        FileManager fileManager = Stub() {
            getInboxFile('file1.json') >> {

                File file = File.createTempFile('file1', 'json')
                file.text = new Gson().toJson(countryData)
                file
            }
            getOutboxFile(outboxFile) >> {

                File file = new File(outboxPath + outboxFile)
                file.text = gson.toJson(countryData)
                return file
            }
            createOutboxFile(outboxFile, gson.toJson(countryData)) >> {

                File file = new File(outboxPath + outboxFile)
                file.setText(gson.toJson(countryData))
                return file
            }
            createInboxFile('file1.json', _) >> {


                File file = new File(inboxPath + inboxFile);
                file.write("");
                return file
            }
        }

        String syncServerDownloadInfoUrl = 'http://dummyServer:8080/app/downloadInfo'
        RestTemplate restTemplate = Stub() {

            getMessageConverters() >> []
            getForObject(syncServerDownloadInfoUrl, String.class) >> {
                gson.toJson(new MockData().getFilteredSampleData("inbox", ["message", "distList"]))
            }

        }





        agentService = new AgentService(dbAccess, dbStore, awakeFileSession, fileManager, restTemplate, syncServerDownloadInfoUrl, serverUploadDir)
        dt.cleanInsert("inbox,outbox")
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
        agentService.distributeMessage("file1.json", "1,2")
        agentService.distributeMessage("file2.json", "3,4")

        then:
        gSql.rows("select * from outbox where upldStatus is NULL").size() == 4
    }

    def "should save data changelog to respective datastore"() {

        given:
        dt.clean("country")


        when:
        agentService.saveLogInfToDatabase(changelog)

        then:
        gSql.rows("select * from country").size() == 1

    }

    def "should get files to be imported"() {

        given:
        dt.cleanInsert("inbox")

        expect:
        agentService.getFilesToImport() == [[id: 1, message: 'file1.json'], [id: 2, message: 'file2.json']]


    }

    def "should get inbox file content"() {

        expect:
        agentService.getInboxFileContents('file1.json') == gson.toJson(countryData)


    }


    def "should get All Logs from the database"() {

        setup:
        dt.cleanInsert("datachangelog")


        expect:
        agentService.getAllLogs() == gson.toJson(countryData)

    }

    def "should get a file name based on the timestamp"() {
        expect:
        assert agentService.createAFileName().endsWith(".json")

    }

    def "should create an outbox file"() {
        given:
        String filename = outboxFile
        String fileContents = gson.toJson(countryData)

        when:
        agentService.createOutboxFile(filename, fileContents)

        then:
        new File(outboxPath + filename).text == fileContents


    }

    def "should save a given file to outbox in database"() {
        given:
        dt.clean('outbox')

        when:
        agentService.saveFileToOutbox(outboxFile)

        then:
        gSql.rows("select * from outbox").size() == 1


    }

    def "should get an outbox file"() {

        when:
        File file = agentService.getOutboxFile(outboxFile)

        then:
        file.text == gson.toJson(countryData)

    }

    def "should get all outbox file to upload"() {


        expect:
        agentService.getOutboxFilesToUpload() == [
                [id: 1, message: 'file1.json'],
                [id: 2, message: 'file2.json']
        ]

    }

    def "should update the status for successful upload"() {


        when:
        agentService.uploadSuccessful('1')
        agentService.uploadSuccessful('2')

        then:
        gSql.rows('select * from outbox where upldStatus is null').size() == 0

    }

    def "should get Download Info from the server"() {


        expect:
        agentService.getDownloadInfo() == [
                [message: 'file1.json', distList: '1,2'],
                [message: 'file2.json', distList: '3,4'],
        ]

    }


    def "should download a given file from server"() {

        when:
        agentService.download(inboxFile)

        then:
        def dir = new File(inboxPath); List files = []
        dir.eachFile {
            files << it
        }
        files.size() == 1

        and:
        new File(inboxPath + inboxFile).text == gson.toJson(countryData)


    }


    def "should upload a given file to server"() {

        given:
        File file = new File(outboxPath + outboxFile)



        when:
        agentService.upload(file)

        then:
        gSql.rows("select * from outbox where message=? and upldStatus=1", [outboxFile]).size() == 1


    }


    def "should update the status of a successfully downloaded file"() {

        when:
        agentService.downloadSuccessful(inboxFile)

        then:
        gSql.rows("select * from inbox where dwnStatus=1").size() == 1


    }


}
