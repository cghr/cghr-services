package org.cghr.dataSync.client

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
class UploadAgentSpec extends Specification {


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared UploadAgent uploadAgent
    @Shared
    List<Map> dataSet
    @Shared
    String file1 = 'file1.json'
    @Shared
    String file2 = 'file2.json'
    @Shared
    String outboxPath = File.createTempDir().absolutePath+'/'
    @Shared
    File localFile = new File(outboxPath + file1)


    def setupSpec() {
        dataSet = new MockData().sampleData.get("outbox")
    }

    def setup() {

        AgentService agentService = Stub() {

            getOutboxFilesToUpload() >> dataSet
            getOutboxFile(file1) >> localFile

            upload(localFile) >> {
                println localFile.name+'File upload successful'
            }

            uploadSuccessful('1') >> {

                gSql.executeUpdate('update outbox set upldStatus=1 where id=?', ['1'])

            }
            uploadSuccessful('2') >> {

                gSql.executeUpdate('update outbox set upldStatus=1 where id=?', ['2'])

            }
        }
        uploadAgent = new UploadAgent(agentService)
        dt.cleanInsert("outbox")

    }


    def "should upload all outbox files to the server with upldStatus null"() {

        when:
        uploadAgent.run()

       then:
       gSql.rows("select * from outbox where upldStatus is not null").size() == 2


    }


}
