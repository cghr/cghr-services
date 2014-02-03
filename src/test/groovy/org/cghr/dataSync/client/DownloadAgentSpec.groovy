package org.cghr.dataSync.client
import groovy.sql.Sql
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification
/**
 * Created by ravitej on 27/1/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class DownloadAgentSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    DownloadAgent downloadAgent
    String remoteFileBasePath = ''
    String localFileBasePath = ''

    def setup() {


        AwakeFileSession awakeFileSession = Stub()
                {
                    download(remoteFileBasePath + 'file1.json', localFileBasePath + 'file1.json') >> {
                        new File(localFileBasePath + 'file1.json')
                    }
                    download(remoteFileBasePath + 'file2.json', localFileBasePath + 'file2.json') >> {
                        new File(localFileBasePath + 'file2.json')
                    }

                }


        AgentService agentService = Stub() {
            getInboxFilesToDownload() >> { gSql.rows("select id,message from inbox where dwnStatus is null") }
        }
        downloadAgent = new DownloadAgent(agentService, awakeFileSession, remoteFileBasePath, localFileBasePath)

        dt.cleanInsert("inbox")

    }


    @Ignore
    def "should download the inbox files from server  whose download status is null"() {
        given:
        File dir = new File(localFileBasePath)
        List files = []

        when:
        downloadAgent.run()


        then:
        dir.eachFile { files << it }
        files.size() == 2
    }

}
