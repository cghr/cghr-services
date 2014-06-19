package org.cghr.dataSync.providers

import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentService
import org.cghr.dataSync.service.SyncUtil
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 13/6/14.
 */
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

    AgentServiceProvider(DbAccess dbAccess, DbStore dbStore, RestTemplate restTemplate, Integer changelogChunkSize, String serverBaseUrl, String downloadInfoPath, String downloadDataBatchPath, String uploadPath, SyncUtil syncUtil, String awakeFileManagerPath, Map fileStoreFactory, String userHome) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.serverBaseUrl = serverBaseUrl
        this.downloadInfoPath = downloadInfoPath
        this.downloadDataBatchPath = downloadDataBatchPath
        this.uploadPath = uploadPath
        this.syncUtil = syncUtil
        this.awakeFileManagerPath = awakeFileManagerPath
        this.fileStoreFactory = fileStoreFactory
        this.userHome = userHome
    }
    //Dynamic Properties
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    AwakeFileSession awakeFileSession

    AgentService provide() {

        createDynamicSyncServerUrls()
        buildAwakeFileSession()

        new AgentService(dbAccess, dbStore, syncServerDownloadInfoUrl, syncServerUploadUrl, restTemplate, changelogChunkSize, syncServerDownloadDataBatchUrl, awakeFileSession, fileStoreFactory, userHome)

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
