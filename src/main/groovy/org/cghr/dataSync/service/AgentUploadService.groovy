package org.cghr.dataSync.service

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

import java.sql.Clob

/**
 * Created by ravitej on 2/7/14.
 */
@TupleConstructor
class AgentUploadService {

    DbAccess dbAccess
    DbStore dbStore
    String syncServerUploadUrl
    RestTemplate restTemplate
    Integer changelogChunkSize

    String getDataChangelogBatch() {

        String sql = "select log from datachangelog where status is null limit $changelogChunkSize"
        List logs = []
        dbStore.eachRow(sql, []) { logs << getLog(it) }
        logs.toString()
    }

    String getLog(row) {
        row.log instanceof Clob ? row.log.getAsciiStream().getText() : row.log
    }

    void postBatch(String changelogBatch) {

        HttpEntity<String> request = constructJsonRequest(changelogBatch)
        restTemplate.postForLocation(syncServerUploadUrl, request)
    }

    HttpEntity<String> constructJsonRequest(String data) {

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        new HttpEntity<String>(data, headers)
    }

    void postBatchSuccessful() {

        dbStore.execute("update datachangelog set status=1 where status is null limit $changelogChunkSize", [])
    }


    Integer getDataChangelogChunks() {

        int pendingLogs = dbAccess.firstRow("select count(*) count from datachangelog where status is null", []).count
        Math.floor(pendingLogs / changelogChunkSize) + 1

    }
}
