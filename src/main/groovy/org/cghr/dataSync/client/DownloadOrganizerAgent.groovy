package org.cghr.dataSync.client

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService
import org.cghr.dataSync.service.DataSyncService

class DownloadOrganizerAgent implements Agent {

    DataSyncService dataSyncService
    AgentService agentService

    DownloadOrganizerAgent(DataSyncService dataSyncService, AgentService agentService) {
        this.dataSyncService = dataSyncService
        this.agentService = agentService


    }


    void run() {

        List files = dataSyncService.getDownloadInfo()
        agentService.saveDownloadInfo(files)
    }


}
