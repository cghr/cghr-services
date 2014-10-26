package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.*
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 13/6/14.
 */

@TupleConstructor(includes = ["dbAccess", "dbStore", "restTemplate", "changelogChunkSize", "serverBaseUrl", "downloadInfoPath", "downloadDataBatchPath", "uploadPath", "syncUtil", "awakeFileManagerPath", "fileStoreFactory", "userHome"])
class AgentServiceProvider {

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

    //Dynamic Properties
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    AwakeFileSession awakeFileSession

    AgentDownloadService agentDownloadService
    AgentUploadService agentUploadService
    AgentMsgDistService agentMsgDistService
    AgentFileUploadservice agentFileUploadservice

    AgentService provide() {

        createDynamicSyncServerUrls()
        buildAwakeFileSession()

        agentDownloadService = getAgentDownloadService()
        agentUploadService = getAgentUploadService()
        agentMsgDistService = getAgentMsgDistService()
        agentFileUploadservice = getAgentFileUploadService()

        new AgentService(agentDownloadService, agentUploadService, agentMsgDistService, agentFileUploadservice)

    }


    AgentDownloadService getAgentDownloadService() {

        new AgentDownloadService(dbAccess, dbStore, syncServerDownloadInfoUrl, syncServerDownloadDataBatchUrl, restTemplate)
    }

    AgentMsgDistService getAgentMsgDistService() {
        new AgentMsgDistService(dbStore, dbAccess)
    }

    AgentUploadService getAgentUploadService() {
        new AgentUploadService(dbAccess, dbStore, syncServerUploadUrl, restTemplate, changelogChunkSize);

    }

    AgentFileUploadservice getAgentFileUploadService() {

        new AgentFileUploadservice(dbAccess, dbStore, awakeFileSession, fileStoreFactory)
    }

    void createDynamicSyncServerUrls() {
        String syncServer = syncUtil.syncServerBaseUrl(serverBaseUrl)
        syncServerDownloadInfoUrl = syncServer + downloadInfoPath + '/' + syncUtil.getRecipientId()
        syncServerUploadUrl = syncServer + uploadPath
        syncServerDownloadDataBatchUrl = syncServer + downloadDataBatchPath

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
            println 'Awake File Manager  Not Found on the Sync Server or Sync Server not Available'
        }
    }


}
