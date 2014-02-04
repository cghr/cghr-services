package org.cghr.dataSync.service
/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    void saveDownloadInfo(List<Map> list) {

    }

    List<Map> getInboxFilesToDownload() {
        null
    }

    List getFilesToDistribute() {
        null
    }

    void distributeMessage(String message, String recepient) {

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
