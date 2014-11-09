package org.cghr.dataSync.client

import groovy.transform.TupleConstructor
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

@TupleConstructor
class DownloadAgent implements Agent {

    AgentService agentService


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
