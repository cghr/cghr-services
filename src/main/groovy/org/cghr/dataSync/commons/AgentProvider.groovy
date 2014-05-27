package org.cghr.dataSync.commons

import groovy.sql.Sql
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.client.DownloadAgent
import org.cghr.dataSync.client.DownloadOrganizerAgent
import org.cghr.dataSync.client.FileUploadAgent
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
    SyncUtil syncUtil
    String awakeFileManagerPath
    Map fileStoreFactory
    String userHome

    AgentProvider(Sql gSql, DbAccess dbAccess, DbStore dbStore, RestTemplate restTemplate, Integer changelogChunkSize, String serverBaseUrl, String downloadInfoPath, String downloadDataBatchPath, String uploadPath, String awakeFileManagerPath,Map fileStoreFactory,String userHome,SyncUtil syncUtil) {
        this.gSql = gSql
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.serverBaseUrl = serverBaseUrl
        this.downloadInfoPath = downloadInfoPath
        this.downloadDataBatchPath = downloadDataBatchPath
        this.uploadPath = uploadPath
        this.awakeFileManagerPath = awakeFileManagerPath
        this.fileStoreFactory=fileStoreFactory
        this.userHome=userHome
        this.syncUtil=syncUtil
    }
//Dynamic Properties
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    AgentService agentService
    AwakeFileSession awakeFileSession

    //Agents
    Agent downloadOrganizerAgent
    Agent downloadAgent
    Agent msgDistAgent
    Agent uploadAgent
    Agent fileUploadAgent

    List<Agent> provideAllAgents() {
        createAgentsDynamically()
        return [downloadOrganizerAgent, downloadAgent, msgDistAgent, uploadAgent,fileUploadAgent]
    }

    void createAgentsDynamically() {
        createDynamicSyncServerUrls()
        buildAwakeFileSession()
        createAgentService()
        downloadOrganizerAgent = new DownloadOrganizerAgent(agentService)
        downloadAgent = new DownloadAgent(agentService)
        msgDistAgent = new MsgDistAgent(agentService)
        uploadAgent = new UploadAgent(agentService)
        fileUploadAgent=new FileUploadAgent(agentService)
    }

    void createAgentService() {

        this.agentService = new AgentService(gSql, dbAccess, dbStore, syncServerDownloadInfoUrl, syncServerUploadUrl, restTemplate, changelogChunkSize, syncServerDownloadDataBatchUrl, awakeFileSession,fileStoreFactory,userHome)

    }

    void createDynamicSyncServerUrls() {
        String syncServer = syncServerBaseUrl()
        syncServerDownloadInfoUrl = syncServer + downloadInfoPath + '/' + getRecipientId()
        syncServerUploadUrl = syncServer + uploadPath
        syncServerDownloadDataBatchUrl = syncServer + downloadDataBatchPath

    }

    String syncServerBaseUrl() {
        String role = dbAccess.getRowAsMap("select role from authtoken order by id desc limit 1", []).role
        String url = (role == 'manager') ? serverBaseUrl : syncUtil.getLocalServerBaseUrl()
        //println 'server base url '+url
        return url
    }

    String getRecipientId() {
        String username = dbAccess.getRowAsMap("select username from authtoken order by id desc limit 1", []).username
        dbAccess.getRowAsMap("select id from user where username=?", [username]).id
    }

    void buildAwakeFileSession() {

        String username = "demo";
        char[] password = ['d', 'e', 'm', 'o']

        // Create the Awake FILE Session to the remote server:
        try {
            this.awakeFileSession = new AwakeFileSession(serverBaseUrl + awakeFileManagerPath, username,
                    password);

        }
        catch (Exception e) {
            println 'Awake File Manager Not Found'
        }
    }


}
