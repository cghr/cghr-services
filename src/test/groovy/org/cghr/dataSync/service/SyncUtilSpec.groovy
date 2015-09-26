package org.cghr.dataSync.service

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class SyncUtilSpec extends Specification {

    SyncUtil syncUtil
    @Autowired
    DbAccess dbAccess

    def setupSpec() {

    }

    def setup() {

        RestTemplate restTemplate = Stub() {
            getForObject('http://192.168.0.100:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.101:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.102:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.103:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.104:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.105:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.106:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.107:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.108:8080/hc/api/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.109:8080/hc/api/status/manager', Map.class) >> {
                throw new RestClientException()
            }

            getForObject('http://192.168.0.110:8080/hc/api/status/manager', Map.class) >> [status: true]
            getRequestFactory() >> new SimpleClientHttpRequestFactory()

        }
        String baseIp = '192.168.0.'
        Integer startNode = 100
        Integer endNode = 120
        Integer port = 8080
        String pathToCheck = 'api/status/manager'
        String appName = 'hc'



        syncUtil = new SyncUtil(dbAccess, restTemplate, baseIp, startNode, endNode, port, pathToCheck, appName)


    }

    def "should get a local server base url"() {

        expect:
        syncUtil.getLocalServerBaseUrl() == 'http://192.168.0.110:8080/hc/'


    }

}