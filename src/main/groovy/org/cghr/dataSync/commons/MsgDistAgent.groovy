package org.cghr.dataSync.commons

import groovy.transform.TupleConstructor
import org.cghr.dataSync.service.AgentService

@TupleConstructor
class MsgDistAgent implements Agent {

    AgentService agentService
    

    public void run() {
        List messages = agentService.getInboxMessagesToDistribute()
        distributeMessages(messages)
    }

    void distributeMessages(List messages) {

        messages.each {
            Map message ->
                getDistributionList(message).each { String recepient ->
                    agentService.distributeMessage(message, recepient)
                }
                agentService.distributeSuccessful(message)
        }

    }

    List getDistributionList(Map message) {
        message.distList.split(",") as List
    }


}
