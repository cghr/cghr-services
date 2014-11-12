package org.cghr.dataSync.commons
import groovy.sql.Sql
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/1/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class MsgDistAgentSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared
    def dataSet
    MsgDistAgent msgDistAgent

    def setupSpec(){

        dataSet=new MockData().sampleData.get('inbox')

    }

    def setup() {

        dt.cleanInsert("inbox")
        dt.clean("outbox")

        gSql.execute("update inbox set impStatus=1")
        dataSet=gSql.rows("select * from inbox order by id")

        AgentService agentService = Stub() {

            getInboxMessagesToDistribute() >> {
                gSql.rows('select * from inbox where distStatus is null and impStatus=1')
            }

            distributeMessage(dataSet[0],'1') >> {
                gSql.executeInsert('insert into outbox(datastore,ref,refId,recipient) values(?,?,?,?)', [dataSet[0].datastore,dataSet[0].ref,dataSet[0].refId, '1'])
            }
            distributeMessage(dataSet[0],'2') >> {
                gSql.executeInsert('insert into outbox(datastore,ref,refId,recipient) values(?,?,?,?)', [dataSet[0].datastore,dataSet[0].ref,dataSet[0].refId, '2'])
            }
            distributeMessage(dataSet[1],'3') >> {
                gSql.executeInsert('insert into outbox(datastore,ref,refId,recipient) values(?,?,?,?)', [dataSet[1].datastore,dataSet[1].ref,dataSet[1].refId, '3'])
            }
            distributeMessage(dataSet[1],'4') >> {
                gSql.executeInsert('insert into outbox(datastore,ref,refId,recipient) values(?,?,?,?)', [dataSet[1].datastore,dataSet[1].ref,dataSet[1].refId, '4'])
            }
            distributeSuccessful(dataSet[0]) >>{

                gSql.execute('update inbox set distStatus=1 where id=?',[1])

            }
            distributeSuccessful(dataSet[1]) >>{

                gSql.execute('update inbox set distStatus=1 where id=?',[2])

            }


        }

        msgDistAgent = new MsgDistAgent(agentService)

    }

    def "should distribute messages to distribution list in each message and update the dist Status"() {

        when:
        msgDistAgent.run()

        then:
        gSql.rows("select * from outbox").size() == 4
        gSql.rows("select * from inbox where distStatus is null").size()==0
    }


}
