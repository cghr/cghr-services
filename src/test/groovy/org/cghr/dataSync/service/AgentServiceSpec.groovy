package org.cghr.dataSync.service
import com.google.gson.Gson
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class AgentServiceSpec extends Specification {

    //General
    @Autowired
    DbTester dt
    @Autowired
    def gSql
    @Shared
    List inboxMessages
    @Shared
    def changelog = [datastore: 'country', data: [id: 1, name: 'india', continent: 'asia']]
    @Shared
    List countryData
    @Shared
    List dataChangeLogs
    @Shared
    Gson gson = new Gson()

    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore

    def setupSpec() {

        inboxMessages = new MockData().sampleData.get("inbox")
        countryData = new MockData().sampleData.get("country")
        dataChangeLogs = new MockData().sampleData.get("datachangelog")
    }


    def setup() {


    }


    def "should "() {


        expect:
        RestTemplate rt=new RestTemplate()
        //String entities=rt.getForObject('http://demo1278634.mockable.io/downloadInfo',String.class)
        Map[] entities=rt.getForObject('http://demo1278634.mockable.io/downloadInfo',Map[].class)
        println entities as List




    }
}