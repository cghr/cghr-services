package org.cghr.dataSync.client
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class DownloadOrganizerAgent implements Agent {


    AgentService agentService

    DownloadOrganizerAgent(AgentService agentService) {
        this.agentService = agentService
    }


    void run() {

        List files = agentService.getDownloadInfo()
        agentService.saveDownloadInfo(files)
    }


}
