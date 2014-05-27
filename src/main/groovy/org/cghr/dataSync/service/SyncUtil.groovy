package org.cghr.dataSync.service

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
    String appName


    SyncUtil(RestTemplate restTemplate, String baseIp, Integer startNode, Integer endNode, Integer port, String pathToCheck,String appName) {
        this.restTemplate = restTemplate
        this.baseIp = baseIp
        this.startNode = startNode
        this.endNode = endNode
        this.port = port
        this.pathToCheck = pathToCheck
        this.appName=appName
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

        try {

            println 'Trying to Connect with fallback safety '
            Map response = restTemplate.getForObject(url, Map.class)
            return response.status

        }
        catch (Exception e) {
            return false
        }


    }

    String constructHttpUrl(String ip) {
        return "http://${ip}:${port}/${appName}/"
    }


}
