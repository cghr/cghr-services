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

    String syncServerBaseUrl(String mainServerBaseUrl) {
        userRole == 'manager' ? mainServerBaseUrl : getLocalServerBaseUrl()
    }

    String getUserRole() {
        dbAccess.firstRow("select role from authtoken order by id desc limit 1", []).role
    }

    String getLocalServerBaseUrl() {
        int syncServerNode = (startNode..endNode).find { isValidSyncServer(it) }
        return constructHttpUrl(baseIp + syncServerNode)
    }

    String getRecipientId() {
        String username = dbAccess.firstRow("select username from authtoken order by id desc limit 1", []).username
        dbAccess.firstRow("select id from user where username=?", [username]).id
    }

    boolean isValidSyncServer(int node) {

        String url = constructStatusCheckUrl(node)

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
        return "http://$ip:$port/$appName/"
    }

    String constructStatusCheckUrl(int node) {
        constructHttpUrl(baseIp + node) + pathToCheck
    }


}
