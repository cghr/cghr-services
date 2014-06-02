package org.cghr.dataSync.service

import com.google.gson.Gson
import groovy.sql.Sql
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

import java.sql.Clob

/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    //Properties injected by Agent Provider
    Sql gSql
    DbAccess dbAccess
    DbStore dbStore
    String syncServerDownloadInfoUrl
    String syncServerUploadUrl
    String syncServerDownloadDataBatchUrl
    RestTemplate restTemplate
    Integer changelogChunkSize
    AwakeFileSession awakeFileSession
    Map fileStoreFactory
    String userHome

    Gson gson = new Gson()

    AgentService(Sql gSql, DbAccess dbAccess, DbStore dbStore, String syncServerDownloadInfoUrl, String syncServerUploadUrl, RestTemplate restTemplate, Integer changelogChunkSize, String syncServerDownloadDataBatchUrl, AwakeFileSession awakeFileSession, Map fileStoreFactory, String userHome) {
        this.gSql = gSql
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.restTemplate = restTemplate
        this.syncServerDownloadInfoUrl = syncServerDownloadInfoUrl
        this.syncServerUploadUrl = syncServerUploadUrl
        this.restTemplate = restTemplate
        this.changelogChunkSize = changelogChunkSize
        this.syncServerDownloadDataBatchUrl = syncServerDownloadDataBatchUrl
        this.awakeFileSession = awakeFileSession
        this.fileStoreFactory = fileStoreFactory
        this.userHome = userHome
    }


    List<Map> getDownloadInfo() {

        restTemplate.getForObject(syncServerDownloadInfoUrl, List.class)
    }


    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list, 'inbox')

    }

    List<Map> getInboxMessagesToDownload() {

        dbAccess.getRowsAsListOfMaps("select * from inbox where impStatus is null", [])

    }

    void downloadAndImport(Map message) {

        String url = syncServerDownloadDataBatchUrl + message.datastore + '/' + message.ref + '/' + message.refId
        List list = restTemplate.getForObject(url, List.class)

        List changelogs = list.collect {
            [datastore: message.datastore, data: it]
        }
        dbStore.saveOrUpdateBatch(changelogs)
    }

    void importSuccessful(Map message) {

        dbStore.saveOrUpdate([id: message.id, impStatus: 1], 'inbox')
    }

    List<Map> getInboxMessagesToDistribute() {

        dbAccess.getRowsAsListOfMaps("select * from inbox where impStatus is not null and distStatus is null", [])
    }

    void distributeMessage(Map message, String recipient) {

        dbStore.saveOrUpdate([datastore: message.datastore, ref: message.ref, refId: message.refId, recipient: recipient], 'outbox')
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

                if (row.log instanceof Clob)
                    logs << row.log.getAsciiStream().getText()
                else
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

        gSql.executeUpdate("update datachangelog set status=1 where status is null limit $changelogChunkSize")
    }

    List getFileChangelogs() {

        gSql.rows("select * from filechangelog where status is null")
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
