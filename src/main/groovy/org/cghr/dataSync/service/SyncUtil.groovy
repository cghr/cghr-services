package org.cghr.dataSync.service

import groovy.transform.CompileStatic
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 5/5/14.
 */
class SyncUtil {


    RestTemplate restTemplate
    String baseIp
    Integer startNode
    Integer endNode
    Integer port
    String pathToCheck


    SyncUtil(RestTemplate restTemplate, String baseIp, Integer startNode, Integer endNode, Integer port, String pathToCheck) {

        this.restTemplate = restTemplate
        this.baseIp = baseIp
        this.startNode = startNode
        this.endNode = endNode
        this.port = port
        this.pathToCheck = pathToCheck
    }

    String getLocalServerBaseUrl() {

        List possibleIps = []
        for (Integer node = startNode; node <= endNode; node++) {
            String baseUrl = constructHttpUrl(baseIp + node)
            if (isValidSyncServer(baseUrl + pathToCheck)) {
                return baseUrl
            }
        }

    }

    boolean isValidSyncServer(String url) {

        Map response = restTemplate.getForObject(url, Map.class)
        response.status

    }

    String constructHttpUrl(String ip) {
        return "http://${ip}:${port}/"
    }


}
