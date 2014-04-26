package org.cghr.dataSync1.service

import com.google.gson.Gson
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate

import java.text.SimpleDateFormat

/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    DbAccess dbAccess
    DbStore dbStore
    AwakeFileSession awakeFileSession
    RestTemplate restTemplate
    String syncServerDownloadInfoUrl
    String serverUploadDir


    Gson gson = new Gson()

    AgentService(DbAccess dbAccess, DbStore dbStore, AwakeFileSession awakeFileSession, RestTemplate restTemplate, String syncServerDownloadInfoUrl,String serverUploadDir) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.awakeFileSession = awakeFileSession
        this.restTemplate = restTemplate
        this.syncServerDownloadInfoUrl = syncServerDownloadInfoUrl
    }

    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list, "inbox")
    }

    List<Map> getInboxFilesToDownload() {

        dbAccess.getRowsAsListOfMaps("select id,message from inbox where dwnStatus is null", [])
    }

    List getInboxFilesToDistribute() {
        dbAccess.getRowsAsListOfMaps("select id,message from inbox where distStatus is null", [])
    }

    void distributeMessage(String message, String recepients) {

        def recepientsArray = recepients.split(",")
        recepientsArray.each {
            recepient ->
                dbStore.saveOrUpdate([message: message, recepient: recepient], "outbox")
        }

    }

    void saveLogInfToDatabase(Map map) {

        dbStore.saveOrUpdate(map.data, map.datastore)
    }

    List<Map> getFilesToImport() {

        dbAccess.getRowsAsListOfMaps("select id,message from inbox where impStatus is null", [])
    }



    String getAllLogs() {

        List result = []

        result = dbAccess.eachRow('select log from datachangelog where status is null', [], result, {

            result << it.log.getAsciiStream().getText()
        })

        gson.toJson(result)

    }

    String createAFileName() {

        new SimpleDateFormat("dd-mm-yy-HH:MM:ss").format(new Date()) + ".json"
    }


    void saveFileToOutbox(String fileName) {

        dbStore.saveOrUpdate([message: fileName], 'outbox')

    }


    List<Map> getOutboxFilesToUpload() {

        dbAccess.getRowsAsListOfMaps("select id,message from outbox where upldStatus is null", [])
    }

    def uploadSuccessful(String fileId) {

        dbStore.saveOrUpdate([id: fileId, upldStatus: 1], "outbox")
    }

    List getDownloadInfo() {


        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter())
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());


        String jsonResponse = restTemplate.getForObject(syncServerDownloadInfoUrl, String.class)
        gson.fromJson(jsonResponse, List.class)

    }



    def upload(File localFile) {

        String remoteFile=serverUploadDir+localFile.getName()
        awakeFileSession.upload(localFile,remoteFile)

    }

    def downloadSuccessful(String filename) {

        dbStore.saveOrUpdate([dwnStatus:'1',message: filename],"inbox")

    }
}
