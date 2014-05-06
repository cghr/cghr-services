package org.cghr.dataSync.commons

import groovy.sql.Sql
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.client.DownloadAgent
import org.cghr.dataSync.client.DownloadOrganizerAgent
import org.cghr.dataSync.client.UploadAgent
import org.cghr.dataSync.service.AgentService
import org.cghr.dataSync.service.SyncUtil
import org.springframework.web.client.RestTemplate

class AgentProvider {


    Sql gSql
    DbAccess dbAccess
    DbStore dbStore
    RestTemplate restTemplate
    Integer changelogChunkSize
    String serverBaseUrl
    String downloadInfoPath
    String downloadDataBatchPath
    String uploadPath

    AgentProvider(Sql gSql, DbAccess dbAccess, DbStore dbStore, RestTemplate restTemplate, Integer changelogChunkSize, String serverBaseUrl, String downloadInfoPath, String downloadDataBatchPath, String uploadPath) {
        this.gSql = gSql
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.serverBaseUrl = serverBaseUrl
        this.downloadInfoPath = downloadInfoPath
        this.downloadDataBatchPath = downloadDataBatchPath
        this.uploadPath = uploadPath
    }
//Dynamic Properties
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    AgentService agentService

    //Agents
    Agent downloadOrganizerAgent
    Agent downloadAgent
    Agent msgDistAgent
    Agent uploadAgent


    List<Agent> provideAllAgents() {
        createAgentsDynamically()
        return [downloadOrganizerAgent, downloadAgent, msgDistAgent, uploadAgent]
    }

    void createAgentsDynamically() {
        createDynamicSyncServerUrls()
        createAgentService()
        downloadOrganizerAgent = new DownloadOrganizerAgent(agentService)
        downloadAgent = new DownloadAgent(agentService)
        msgDistAgent = new MsgDistAgent(agentService)
        uploadAgent = new UploadAgent(agentService)
    }

    void createAgentService() {
        this.agentService = new AgentService(gSql, dbAccess, dbStore, syncServerDownloadInfoUrl, syncServerUploadUrl, restTemplate, changelogChunkSize, syncServerDownloadDataBatchUrl)

    }

    void createDynamicSyncServerUrls() {
        String syncServer = syncServerBaseUrl()
        syncServerDownloadInfoUrl = syncServer + downloadInfoPath + File.separator + getRecipientId()
        syncServerUploadUrl = syncServer + uploadPath
        syncServerDownloadDataBatchUrl = syncServer + downloadDataBatchPath
    }

    String syncServerBaseUrl() {
        String role = dbAccess.getRowAsMap("select role from authtoken order by id desc limit 1",[]).role
        role == 'manager' ? serverBaseUrl : new SyncUtil().getLocalServerBaseUrl()
    }

    String getRecipientId() {
        String username = dbAccess.getRowAsMap("select username from authtoken order by id desc limit 1",[]).username
        dbAccess.getRowAsMap("select id from user where username=?", [username]).id
    }


}
