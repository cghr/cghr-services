package org.cghr.dataSync.client

import groovy.transform.CompileStatic
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

@CompileStatic
class UploadAgent implements Agent {


    AgentService agentService

    UploadAgent(AgentService agentService) {

        this.agentService = agentService
    }

    public void run() {

        Integer chunks = agentService.getDataChangelogChunks()
        chunks.times {
            agentService.postBatch(agentService.getDataChangelogBatch())
            agentService.postBatchSuccessful()
        }


    }


}
