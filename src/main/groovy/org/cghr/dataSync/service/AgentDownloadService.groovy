package org.cghr.dataSync.service

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 2/7/14.
 */
class AgentDownloadService {

    DbAccess dbAccess
    DbStore dbStore
    String syncServerDownloadInfoUrl
    String syncServerDownloadDataBatchUrl
    RestTemplate restTemplate

    AgentDownloadService(DbAccess dbAccess, DbStore dbStore, String syncServerDownloadInfoUrl, String syncServerDownloadDataBatchUrl, RestTemplate restTemplate) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.syncServerDownloadInfoUrl = syncServerDownloadInfoUrl
        this.syncServerDownloadDataBatchUrl = syncServerDownloadDataBatchUrl
        this.restTemplate = restTemplate
    }

    List<Map> getDownloadInfo() {

        restTemplate.getForObject(syncServerDownloadInfoUrl, List.class)
    }


    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list, 'inbox')

    }

    List<Map> getInboxMessagesToDownload() {

        dbAccess.rows("select * from inbox where impStatus is null", [])

    }

    void downloadAndImport(Map message) {

        String url = syncServerDownloadDataBatchUrl + message.datastore + '/' + message.ref + '/' + message.refId
        List list = restTemplate.getForObject(url, List.class)
        list.each { dbStore.saveOrUpdate(it, message.datastore) }
    }


    void importSuccessful(Map message) {

        dbStore.saveOrUpdate([id: message.id, impStatus: 1], 'inbox')
    }

    List<Map> getInboxMessagesToDistribute() {

        dbAccess.rows("select * from inbox where impStatus is not null and distStatus is null", [])
    }

    void distributeMessage(Map message, String recipient) {

        dbStore.saveOrUpdate([datastore: message.datastore, ref: message.ref, refId: message.refId, recipient: recipient], 'outbox')
    }

    void distributeSuccessful(Map message) {
        dbStore.saveOrUpdate([id: message.id, distStatus: 1], 'inbox')
    }
}
