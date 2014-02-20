package org.cghr.dataSync.service

import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore

/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    DbAccess dbAccess
    DbStore dbStore
    AwakeFileSession awakeFileSession

    AgentService(DbAccess dbAccess, DbStore dbStore, AwakeFileSession awakeFileSession) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.awakeFileSession = awakeFileSession
    }

    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list,"inbox")
    }

    List<Map> getInboxFilesToDownload() {

        dbAccess.getRowsAsListOfMaps("select id,message from inbox where dwnStatus is null",[])
    }

    List getInboxFilesToDistribute() {
        dbAccess.getRowsAsListOfMaps("select id,message from inbox where distStatus is null",[])
    }

    void distributeMessage(String message, String recepients) {

        def recepientsArray=recepients.split(",")
        recepientsArray.each {
            recepient ->
            dbStore.saveOrUpdate([message:message,recepient:recepient],"outbox")
        }

    }

    void saveLogInfToDatabase(Map map) {

    }

    List<Map> getFilesToImport() {}

    String getInboxFileContents(String s) {

        null
    }

    String getAllLogs() {}

    String createAFileName() {}

    def createOutboxFile(String fileName, String fileContents) {

    }

    void saveFileToOutbox(String fileName) {

    }

    File getOutboxFile(String filename) {
        null
    }

    List<Map> getOutboxFilesToUpload() {
        null
    }

    def uploadSuccessful(String filename) {

    }

    List getDownloadInfo() {
        null
    }

    def download(String filename) {

    }
    def upload(File localFile)
    {

    }

   def downloadSuccessful(String filename) {

    }
}
