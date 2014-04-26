package org.cghr.dataSync1.commons

import org.cghr.dataSync1.service.AgentService

class MsgDistAgent implements Agent {

    AgentService agentService

    MsgDistAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {

        List distFiles =agentService.getInboxFilesToDistribute()
        println distFiles
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
