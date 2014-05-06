package org.cghr.dataSync.client

import groovy.transform.CompileStatic
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

@CompileStatic
class DownloadAgent implements Agent {

    AgentService agentService

    DownloadAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {
        List<Map> messages = agentService.getInboxMessagesToDownload()
        downloadAndImportMessages(messages)

    }

    void downloadAndImportMessages(List<Map> messages) {
        messages.each {
             Map message ->
                agentService.downloadAndImport(message)
                agentService.importSuccessful(message)
        }
    }
}
