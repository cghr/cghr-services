package org.cghr.dataSync.commons

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.client.DownloadAgent
import org.cghr.dataSync.client.DownloadOrganizerAgent
import org.cghr.dataSync.client.UploadAgent
import org.cghr.dataSync.service.AgentService
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
        syncServerDownloadInfoUrl = syncServer + downloadInfoPath
        syncServerUploadUrl = syncServer + uploadPath
        syncServerDownloadDataBatchUrl = syncServer + downloadDataBatchPath


    }
    //Todo
    String syncServerBaseUrl() {

    }


}
