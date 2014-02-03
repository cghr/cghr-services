package org.cghr.dataSync.client

import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class DownloadAgent implements Agent {

    AgentService agentService
    AwakeFileSession awakeFileSession
    String remoteFileBasePath
    String localFileBasePath

    DownloadAgent(AgentService agentService, AwakeFileSession awakeFileSession, String remoteFileBasePath, String localFileBasePath) {

        this.agentService = agentService
        this.awakeFileSession = awakeFileSession
        this.remoteFileBasePath = remoteFileBasePath
        this.localFileBasePath = localFileBasePath
    }

    public void run() {

        List<Map> files = agentService.getInboxFilesToDownload()
        downloadFiles(files)

    }

    void downloadFiles(List<Map> files) {
        files.each {
            fileInfo ->
                String remoteFile = remoteFileBasePath + fileInfo.message
                File localFile = new File(localFileBasePath + fileInfo.message)
                awakeFileSession.download (remoteFile, localFile)

        }
    }
}
