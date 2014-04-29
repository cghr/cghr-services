package org.cghr.dataSync.client
import groovy.sql.Sql
import org.cghr.context.SpringContext
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/4/14.
 */
class UploadAgentSpec extends Specification {

    UploadAgent uploadAgent

    Sql gSql=SpringContext.sql
    DbTester dt=SpringContext.dbTester

    @Shared
    List dataSet
    @Shared
    List countryData

    def setupSpec() {

    }


    def setup() {

        AgentService agentService = Stub() {

            getDataChangelogChunks() >> 1
            getDataChangelogBatch() >> {

                List<Map> logs = []
                gSql.eachRow("select log from datachangelog where status is null limit 20") {
                    row ->
                        logs << row.log.getAsciiStream().getText()
                }
                logs.toString()

            }
            postBatchSuccessful() >> {
                gSql.execute('update datachangelog set status=1 where status is null limit 20')

            }


        }
        uploadAgent = new UploadAgent(agentService)
        dt.cleanInsert('datachangelog')

    }

    def "should get changelogs in chunks and post each chunk of data to the server and update the status"() {

        when:
        uploadAgent.run()

        then:
        gSql.rows("select * from datachangelog where status is null").size() == 0

    }
}