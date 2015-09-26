package org.cghr.dataSync.client

import groovy.transform.TupleConstructor
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

@TupleConstructor
class DownloadOrganizerAgent implements Agent {

    AgentService agentService

    void run() {
        List entities = agentService.getDownloadInfo()
        agentService.saveDownloadInfo(entities)
    }


}
