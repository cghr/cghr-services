package org.cghr.dataSync.client
import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
/**
 * Created by ravitej on 3/2/14.
 */
class UploadOrganizerAgentSpec {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared List<Map> dataSet
    def setupSpec() {
        //dataSet = new MockData().sampleData.get("")
    }

    def setup() {



    }

    def "should organize files to upload"() {


    }


}
