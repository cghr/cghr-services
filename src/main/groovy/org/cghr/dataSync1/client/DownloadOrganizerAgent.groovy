package org.cghr.dataSync1.client
import org.cghr.dataSync1.commons.Agent
import org.cghr.dataSync1.service.AgentService

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
