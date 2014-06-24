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

    void distributeMessages(List msgs) {

        List messages = getMessagesWithDistributionList(msgs)
        messages.each {
            Map message ->
                List<String> recepients = getDistributionList(message)
                recepients.each { agentService.distributeMessage(message, it) }

                agentService.distributeSuccessful(message)
        }

    }

    List getDistributionList(Map message) {
        message.distList.split(",") as List
    }

    List getMessagesWithDistributionList(List msgs) {
        msgs.findAll { it.distList }
    }

}
