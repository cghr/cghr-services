package org.cghr.dataSync.client

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.dataSync.service.AgentService
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/4/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class FileUploadAgentSpec extends Specification {

    FileUploadAgent fileUploadAgent

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

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
            getFileChangelogs() >> {
                gSql.rows("select * from filechangelog where status is null")
            }
            fileUploadSuccessful(1) >> {
                gSql.execute('update filechangelog set status=1 where id=1')
            }
            fileUploadSuccessful(2) >> {
                gSql.execute('update filechangelog set status=1 where id=2')
            }
            fileUploadSuccessful(3) >> {
                gSql.execute('update filechangelog set status=1 where id=3')
            }


        }
        fileUploadAgent = new FileUploadAgent(agentService)
        dt.cleanInsert('filechangelog')

    }

    def "should upload the files and update the status"() {

        when:
        fileUploadAgent.run()

        then:
        gSql.rows("select * from filechangelog where status is null").size() == 0

    }

    def "should not upload the files when there's and exception "() {

        when:
        fileUploadAgent.run()

        then:
        gSql.rows("select * from filechangelog where status is null").size() == 0

    }
}