package org.cghr.dataSync.service

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    Sql gSql
    DbAccess dbAccess
    DbStore dbStore
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    RestTemplate restTemplate
    Integer changelogChunkSize


    Gson gson = new Gson()

    AgentService(Sql gSql, DbAccess dbAccess, DbStore dbStore, String syncServerDownloadInfoUrl, String syncServerUploadUrl, RestTemplate restTemplate, Integer changelogChunkSize, String syncServerDownloadDataBatchUrl) {
        this.gSql = gSql
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.restTemplate = restTemplate
        this.syncServerDownloadInfoUrl = syncServerDownloadInfoUrl
        this.syncServerUploadUrl = syncServerUploadUrl
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.syncServerDownloadDataBatchUrl = syncServerDownloadDataBatchUrl
    }


    List<Map> getDownloadInfo() {

        Map[] downloadInfo = restTemplate.getForObject(syncServerDownloadInfoUrl, Map[].class)
        downloadInfo as List
    }


    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list, 'inbox')

    }

    List<Map> getInboxMessagesToDownload() {

        dbAccess.getRowsAsListOfMaps("select * from inbox where impStatus is null", [])

    }

    void downloadAndImport(Map message) {

        String url = syncServerDownloadDataBatchUrl + message.datastore + File.separator + message.ref + File.separator + message.refId
        println 'batch url '
        println url
        Map[] data = restTemplate.getForObject(url, Map[].class)

        List<Map<String, String>> list = data as List

        List chagenlogs = list.collect {
            [datastore: message.datastore, data: it]
        }
        dbStore.saveOrUpdateBatch(chagenlogs)
    }

    void importSuccessful(Map message) {

        dbStore.saveOrUpdate([id: message.id, impStatus: 1], 'inbox')
    }

    List<Map> getInboxMessagesToDistribute() {

        dbAccess.getRowsAsListOfMaps("select * from inbox where impStatus is not null and distStatus is null", [])
    }

    void distributeMessage(Map message, String recipient) {

        dbStore.saveOrUpdate([datastore: message.datastore, ref: message.ref, refId: message.refId, recepient: recipient], 'outbox')
    }

    void distributeSuccessful(Map message) {
        dbStore.saveOrUpdate([id: message.id, distStatus: 1], 'inbox')

    }

    Integer getDataChangelogChunks() {

        Integer pendingLogs = dbAccess.getRowAsMap("select count(*) count from datachangelog where status is null", []).count
        Math.floor(pendingLogs / changelogChunkSize) + 1

    }

    String getDataChangelogBatch() {

        List logs = []

        def sql = "select log from datachangelog where status is null limit $changelogChunkSize".toString()
        gSql.eachRow(sql) {
            row ->
                logs << row.log.getAsciiStream().getText()
        }
        logs.toString()
    }

    void postBatch(String changelogBatch) {

        //restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter())
        //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> request = new HttpEntity<String>(changelogBatch, headers)
        restTemplate.postForLocation(syncServerUploadUrl, request)
    }

    void postBatchSuccessful() {

        gSql.executeUpdate("update datachangelog set status=1 where status is null limit $changelogChunkSize")
    }


}
