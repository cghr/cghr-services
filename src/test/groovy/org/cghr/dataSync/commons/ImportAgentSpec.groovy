package org.cghr.dataSync.commons

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
class ImportAgentSpec extends Specification {


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt


    ImportAgent importAgent
    @Shared
    List<Map> dataSet

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
    }

    def setup() {

        AgentService agentService = Stub() {
            getFilesToImport() >> {
                gSql.rows("select id,message from inbox where impStatus is null")
            }
            saveLogInfToDatabase([datastore: 'country', data: dataSet[0]]) >> {
                gSql.execute("insert into country values(?,?,?)", dataSet[0].values() as List)
            }
            saveLogInfToDatabase([datastore: 'country', data: dataSet[1]]) >> {
                gSql.execute("insert into country values(?,?,?)", dataSet[0].values() as List)
            }
            saveLogInfToDatabase([datastore: 'country', data: dataSet[2]]) >> {
                gSql.execute("insert into country values(?,?,?)", dataSet[0].values() as List)
            }
            getInboxFileContents('file1.json') >> {
                List list = [[datastore: 'country', data: dataSet[0]], [datastore: 'country', data: dataSet[1]]]
                new Gson().toJson(list)
            }
            getInboxFileContents('file2.json') >> {
                List list = [[datastore: 'country', data: dataSet[2]]]
                new Gson().toJson(list)
            }
        }

        importAgent = new ImportAgent(agentService)
        dt.cleanInsert("inbox")
        dt.clean("country")

    }

    def "should import the downloaded inbox files"() {

        when:
        importAgent.run()

        then:
        gSql.rows("select * from country").size() == 3


    }

}
