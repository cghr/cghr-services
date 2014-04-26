package org.cghr.dataSync1.client

import org.cghr.dataSync1.commons.Agent
import org.cghr.dataSync1.service.AgentService

class UploadOrganizerAgent implements Agent {


    AgentService agentService

    UploadOrganizerAgent(AgentService agentService) {

        this.agentService = agentService
    }

    public void run() {

        String fileContents = agentService.getAllLogs()
        String fileName=agentService.createAFileName()

        agentService.createOutboxFile(fileName,fileContents)
        agentService.saveFileToOutbox(fileName)
    }


}
