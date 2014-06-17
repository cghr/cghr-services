package org.cghr.dataSync.commons

import org.cghr.dataSync.service.AgentService

class MsgDistAgent implements Agent {

    AgentService agentService

    MsgDistAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {

        List messages = agentService.getInboxMessagesToDistribute()
        distributeMessages(messages)

    }

    void distributeMessages(List messages) {

        messages.each {
            Map message ->
                if (!message.distList) return

                List<String> recepients = message.distList.split(",") as List
                recepients.each { agentService.distributeMessage(message, it) }

                agentService.distributeSuccessful(message);
        }

    }

}
