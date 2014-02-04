package org.cghr.dataSync.client

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class DownloadAgent implements Agent {

    AgentService agentService

    DownloadAgent(AgentService agentService) {

        this.agentService = agentService
    }

    public void run() {

        List<Map> files = agentService.getInboxFilesToDownload()
        downloadFiles(files)

    }

    void downloadFiles(List<Map> files) {
        files.each {
            fileInfo ->
                String filename=fileInfo.message
                agentService.download(filename)
                agentService.downloadSuccessful(filename)
        }
    }
}
