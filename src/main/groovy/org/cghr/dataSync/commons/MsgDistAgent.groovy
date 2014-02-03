package org.cghr.dataSync.commons

import org.cghr.dataSync.service.AgentService

class MsgDistAgent implements Agent {

    AgentService agentService

    MsgDistAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {

        List distFiles =agentService.getFilesToDistribute()
        distributeMessages(distFiles)

    }

    void distributeMessages(List files) {

        files.each {
            fileInfo ->
                List recepients = fileInfo.distList.split(",") as List
                recepients.each {
                    recepient -> agentService.distributeMessage(fileInfo.message,recepient)
                }
        }

    }

}
