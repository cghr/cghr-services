package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentDownloadService
import org.cghr.dataSync.service.SyncUtil
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 14/11/14.
 */
@TupleConstructor
class AgentDownloadServiceProvider {

    DbAccess dbAccess
    DbStore dbStore
    RestTemplate restTemplate
    String serverBaseUrl
    String downloadInfoPath
    String downloadDataBatchPath
    SyncUtil syncUtil


    def provide() {

        String baseUrl = getSyncServerBaseUrl()
        String syncServerDownloadInfoUrl = baseUrl + downloadInfoPath + '/' + syncUtil.getRecipientId()
        String syncServerDownloadDataBatchUrl = baseUrl + downloadDataBatchPath
        new AgentDownloadService(dbAccess, dbStore, syncServerDownloadInfoUrl, syncServerDownloadDataBatchUrl, restTemplate)

    }

    String getSyncServerBaseUrl() {

        syncUtil.syncServerBaseUrl(serverBaseUrl)
    }

}
