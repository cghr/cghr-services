package org.cghr.dataSync.util

import com.google.gson.Gson
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification


/**
 * Created by ravitej on 24/2/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class FileManagerSpec extends Specification {

    FileManager fileManager
    @Shared
    String inboxPath = File.createTempDir().absolutePath + "/"
    @Shared
    String outboxPath = File.createTempDir().absolutePath + "/"
    @Shared
    String inboxFileName = 'file1.json'
    @Shared
    String outboxFileName = 'file1.json'

    @Shared
    Gson gson = new Gson()

    @Autowired
    def gSql
    @Autowired
    DbTester dt
    @Shared List dataSet


    def setupSpec() {

        dataSet=new MockData().sampleData.get("country")
    }

    def setup() {


        fileManager = new FileManager(inboxPath, outboxPath)
    }

    def "should get Inbox File"() {
        given:
        new File(inboxPath + inboxFileName).setText(gson.toJson(dataSet))


        when:
        File file = fileManager.getInboxFile(inboxFileName)

        then:
        file.text==gson.toJson(dataSet)


    }

    def "should get outbox file"() {

        given:
        new File(outboxPath + outboxFileName).setText(gson.toJson(dataSet))


        when:
        File file = fileManager.getOutboxFile(outboxFileName)

        then:
        file.text==gson.toJson(dataSet)


    }

    def "should create an Inbox file with given content"() {

        when:
        fileManager.createInboxFile(inboxFileName,"some content")

        then:
        new File(inboxPath+inboxFileName).text=="some content"


    }
    def "should create an Outbox file with given content"() {

        when:
        fileManager.createOutboxFile(outboxFileName,"some content")

        then:
        new File(outboxPath+outboxFileName).text=="some content"


    }




}