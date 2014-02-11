package org.cghr.dataSync.client
import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 3/2/14.
 */

@ContextConfiguration(locations = "classpath:spring-context.xml")
class UploadOrganizerAgentSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared
    List<Map> dataSet
    @Shared UploadOrganizerAgent uploadOrganizerAgent
    @Shared def outboxPath =File.createTempDir().absolutePath+'/'
    @Shared def fileName = '15-2014-01-01-01-01-01.json'
    @Shared String fileContents


    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
        fileContents=new Gson().toJson(dataSet)

    }

    def setup() {


        AgentService agentService = Stub() {
            getAllLogs() >> fileContents

            createAFileName() >> fileName

            createOutboxFile(fileName,fileContents) >> {

                File file=new File(outboxPath+fileName)
                file.write(fileContents)
            }

            saveFileToOutbox(fileName) >> {
                gSql.execute("insert into outbox(id,message) values(?,?)",[1,fileName])
            }

        }

        uploadOrganizerAgent = new UploadOrganizerAgent(agentService)

        dt.clean("outbox")
        dt.cleanInsert("datachangelog")
    }

    def "should organize files to upload"() {

        when:
        uploadOrganizerAgent.run()

        then:
        gSql.rows("select  * from outbox where upldStatus is null").size() == 1
        def dir = new File(outboxPath); List files = []
        dir.eachFile {
            files << it
        }
        files.size() == 1

    }


}
