package org.cghr.dataSync.service

import org.awakefw.file.api.client.AwakeFileSession
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
class AgentUploadService {

    DbAccess dbAccess
    DbStore dbStore
    String syncServerUploadUrl
    RestTemplate restTemplate
    Integer changelogChunkSize
    AwakeFileSession awakeFileSession
    Map fileStoreFactory

    AgentUploadService(DbAccess dbAccess, DbStore dbStore, String syncServerUploadUrl, RestTemplate restTemplate, Integer changelogChunkSize, AwakeFileSession awakeFileSession, Map fileStoreFactory) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.syncServerUploadUrl = syncServerUploadUrl
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.awakeFileSession = awakeFileSession
        this.fileStoreFactory = fileStoreFactory
    }

    Integer getDataChangelogChunks() {

        Integer pendingLogs = dbAccess.firstRow("select count(*) count from datachangelog where status is null", []).count
        Math.floor(pendingLogs / changelogChunkSize) + 1

    }

    String getDataChangelogBatch() {

        List logs = []

        def sql = "select log from datachangelog where status is null limit $changelogChunkSize"

        dbStore.eachRow(sql, []) {
            row ->

                if (row.log instanceof Clob) //For H2 like Database
                    logs << row.log.getAsciiStream().getText()
                else                         //For Mysql like Database
                    logs << row.log

        }
        logs.toString()
    }

    void postBatch(String changelogBatch) {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> request = new HttpEntity<String>(changelogBatch, headers)
        restTemplate.postForLocation(syncServerUploadUrl, request)
    }

    void postBatchSuccessful() {

        dbStore.execute("update datachangelog set status=1 where status is null limit $changelogChunkSize", [])
    }

    List getFileChangelogs() {

        dbAccess.rows("select * from filechangelog where status is null", [])
    }

    void fileUploadSuccessful(Integer id) {
        dbStore.saveOrUpdate([id: id, status: 1], 'filechangelog')
    }

    void uploadFile(Map fileInfo) {

        String path = ((Map) fileStoreFactory.get(fileInfo.filestore)).get(fileInfo.fileId)
        //String remoteFile = path + '/' + fileInfo.filename
        String type = ((String) fileInfo.fileId).toLowerCase()
        String remoteFile = ''

        if (type.contains('consent'))
            remoteFile = '/hcDemo/repo/images/consent'
        else if (type.contains('photo'))
            remoteFile = '/hcDemo/repo/images/photo'
        else if (type.contains('photoId'))
            remoteFile = '/hcDemo/repo/images/photoId'

        remoteFile = remoteFile + '/' + fileInfo.filename
        File file = new File(path + '/' + fileInfo.filename)
        println 'file to upload'
        println file
        awakeFileSession.upload(file, remoteFile)

    }
}
