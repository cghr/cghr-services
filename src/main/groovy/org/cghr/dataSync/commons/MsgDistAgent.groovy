package org.cghr.dataSync.commons

import groovy.transform.Memoized
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

        messages.each { Map message ->
            distributeOneMessage(message)
            agentService.distributeSuccessful(message)
        }

    }

    void distributeOneMessage(Map message) {
        getDistributionList(message).each { String recepient ->
            agentService.distributeMessage(message, recepient)
        }
    }

    @Memoized
    List getDistributionList(Map message) {
        message.distList.split(",") as List
    }


}
