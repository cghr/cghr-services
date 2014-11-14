package org.cghr.dataSync.providers
import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentUploadService
import org.cghr.dataSync.service.SyncUtil
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 14/11/14.
 */
@TupleConstructor
class AgentUploadServiceProvider {

    DbAccess dbAccess
    DbStore dbStore
    RestTemplate restTemplate
    Integer changelogChunkSize
    String serverBaseUrl
    String uploadPath
    SyncUtil syncUtil


    def provide() {

        String syncServerUploadUrl = getSyncServerBaseUrl() + uploadPath
        new AgentUploadService(dbAccess, dbStore, syncServerUploadUrl, restTemplate, changelogChunkSize);

    }

    String getSyncServerBaseUrl() {

        return syncUtil.syncServerBaseUrl(serverBaseUrl)
    }

}
