package org.cghr.dataSync.client
import groovy.sql.Sql
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/1/14.
 */
class DownloadAgentSpec extends Specification {

    @Shared
    Sql gSql
    @Shared
    DbTester dt

    DownloadAgent downloadAgent
    String fileBasePath = ''

    def setupSpec() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-context.xml")
        gSql = appContext.getBean("gSql")
        dt = appContext.getBean("dt")


    }

    def setup() {

        DbAccess dbAccess = Stub() {}
        AwakeFileSession awakeFileSession = Stub() {}

        downloadAgent = new DownloadAgent(dbAccess, awakeFileSession, fileBasePath)

        dt.cleanInsert("inbox")

    }

   @Ignore
    def "should download the inbox files from server  whose download status is null"() {
        given:
        File dir=new File(fileBasePath)
        List files=[]

        when:
        downloadAgent.run()


        then:
        dir.eachFile { files << it }
        files.size()==2





    }

    @Ignore
    def "should do 2"() {

    }

}