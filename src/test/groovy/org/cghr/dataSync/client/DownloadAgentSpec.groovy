package org.cghr.dataSync.client

import groovy.sql.Sql
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
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
    @Shared
    String inboxPath = System.getProperty("user.home") + "/bhss/inbox/"
    @Shared
    String file1 = 'file1.json'
    @Shared
    String file2 = 'file2.json'

    def setup() {

        AgentService agentService = Stub() {

            getInboxFilesToDownload() >> { gSql.rows("select id,message from inbox where dwnStatus is null") }

            download(file1) >> {
                new File(inboxPath + file1).createNewFile()
            }
            download(file2) >> {
                new File(inboxPath + file2).createNewFile()
            }
            downloadSuccessful(file1) >> {
                gSql.executeUpdate('update inbox set dwnStatus=1 where message=?',[file1])
            }
            downloadSuccessful(file2) >> {
                gSql.executeUpdate('update inbox set dwnStatus=1 where message=?',[file2])
            }


        }
        downloadAgent = new DownloadAgent(agentService)

        dt.cleanInsert("inbox")

    }


    def "should download the inbox files from server  whose download status is null"() {
        given:
        File dir = new File(inboxPath)
        List files = []

        when:
        downloadAgent.run()


        then:
        dir.eachFile { files << it }
        files.size() == 2
        gSql.rows("select * from inbox where dwnStatus=1").size()==2
    }

}
