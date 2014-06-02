package org.cghr.dataSync.client

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class DownloadAgent implements Agent {

    AgentService agentService

    DownloadAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {
        downloadAndImportMessages(agentService.getInboxMessagesToDownload())

    }

    void downloadAndImportMessages(List<Map> messages) {
        messages.each {
            agentService.downloadAndImport(it)
            agentService.importSuccessful(it)
        }
    }
}
