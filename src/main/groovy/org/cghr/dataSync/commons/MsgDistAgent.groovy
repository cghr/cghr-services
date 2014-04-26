package org.cghr.dataSync.commons

import org.cghr.dataSync.service.AgentService

class MsgDistAgent implements Agent {

    AgentService agentService

    MsgDistAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {

        List distFiles = agentService.getInboxMessagesToDistribute()
        distributeMessages(distFiles)

    }

    void distributeMessages(List messages) {

        messages.each {
            message ->
                List recepients = message.distList.split(",") as List
                recepients.each {
                    recepient ->
                        agentService.distributeMessage(message, recepient)
                }
                agentService.distributeSuccessful(message);
        }

    }

}
