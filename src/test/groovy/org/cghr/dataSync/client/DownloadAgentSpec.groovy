package org.cghr.dataSync.client

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
 * Created by ravitej on 26/4/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class DownloadAgentSpec extends Specification {

    DownloadAgent downloadAgent

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared
    List dataSet
    @Shared
    List countryData

    def setupSpec() {
        dataSet = new MockData().sampleData.get("inbox")
        countryData = new MockData().sampleData.get("country")
    }


    def setup() {

        AgentService agentService = Stub() {

            downloadAndImport(dataSet[0]) >> {

                gSql.execute("insert into country values(?,?,?)", [1, 'india', 'asia'])

            }
            downloadAndImport(dataSet[1]) >> {

                gSql.execute("insert into country values(?,?,?)", [2, 'pakistan', 'asia'])

            }
            importSuccessful(dataSet[0]) >> {
                gSql.execute("update inbox set impStatus=1 where id=?", [1])

            }
            importSuccessful(dataSet[1]) >> {
                gSql.execute("update inbox set impStatus=1 where id=?", [2])

            }

        }
        downloadAgent = new DownloadAgent(agentService)
        dt.cleanInsert('inbox')
        dt.clean('country')
    }

    def "should download and import the data from the server"() {

        when:
        downloadAgent.downloadAndImportMessages(dataSet)

        then:
        gSql.rows("select * from country").size() == 2
        gSql.rows("select * from inbox where impStatus is not null").size() == 2


    }

}