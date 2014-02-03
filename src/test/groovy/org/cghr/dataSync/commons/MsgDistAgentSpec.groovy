package org.cghr.dataSync.commons

import groovy.sql.Sql
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by ravitej on 27/1/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class MsgDistAgentSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    MsgDistAgent msgDistAgent


    def setup() {

        dt.cleanInsert("inbox")
        dt.clean("outbox")

        AgentService agentService = Stub() {

            getFilesToDistribute() >> {
                gSql.rows('select id,message,distList from inbox where distStatus is null')
            }

            distributeMessage('file1.json','1') >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1', '1'])
            }
            distributeMessage('file1.json','2') >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file1', '2'])
            }
            distributeMessage('file2.json','3') >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file2', '3'])
            }
            distributeMessage('file2.json','4') >> {
                gSql.executeInsert('insert into outbox(message,recepient) values(?,?)', ['file2', '4'])
            }


        }

        msgDistAgent = new MsgDistAgent(agentService)

    }

    def "should distribute messages to distribution list in each message"() {

        when:
        msgDistAgent.run()

        then:
        gSql.rows("select * from outbox").size() == 4
    }


}
