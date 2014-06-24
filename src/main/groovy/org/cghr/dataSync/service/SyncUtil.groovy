package org.cghr.dataSync.service

import org.cghr.commons.db.DbAccess
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 5/5/14.
 */
class SyncUtil {


    DbAccess dbAccess
    RestTemplate restTemplate
    String baseIp
    Integer startNode
    Integer endNode
    Integer port
    String pathToCheck
    String appName


    SyncUtil(DbAccess dbAccess, RestTemplate restTemplate, String baseIp, Integer startNode, Integer endNode, Integer port, String pathToCheck, String appName) {
        this.dbAccess = dbAccess
        this.restTemplate = restTemplate
        this.baseIp = baseIp
        this.startNode = startNode
        this.endNode = endNode
        this.port = port
        this.pathToCheck = pathToCheck
        this.appName = appName
    }

    String syncServerBaseUrl(String serverBaseUrl) {
        String role = dbAccess.firstRow("select role from authtoken order by id desc limit 1", []).role
        String url = (role == 'manager') ? serverBaseUrl : getLocalServerBaseUrl()
        return url
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

    String getRecipientId() {
        String username = dbAccess.firstRow("select username from authtoken order by id desc limit 1", []).username
        dbAccess.firstRow("select id from user where username=?", [username]).id
    }

    boolean isValidSyncServer(String url) {

        try {

            println 'checking for valid sync server ' + url
            Map response = restTemplate.getForObject(url, Map.class)
            return response.status

        }
        catch (Exception e) {
            println 'exception while accessing'
            return false
        }


    }

    String constructHttpUrl(String ip) {
        return "http://${ip}:${port}/${appName}/"
    }


}
