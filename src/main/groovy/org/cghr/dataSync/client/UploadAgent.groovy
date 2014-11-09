package org.cghr.dataSync.client

import groovy.transform.TupleConstructor
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

@TupleConstructor
class UploadAgent implements Agent {

    AgentService agentService

    public void run() {

        Integer chunks = agentService.getDataChangelogChunks()

        chunks.times {
            agentService.postBatch(agentService.getDataChangelogBatch())
            agentService.postBatchSuccessful()
        }


    }


}
