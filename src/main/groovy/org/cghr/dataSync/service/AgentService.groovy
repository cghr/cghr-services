package org.cghr.dataSync.service
/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    @Delegate
    AgentUploadService agentUploadService

    @Delegate
    AgentDownloadService agentDownloadService

    AgentService(AgentDownloadService agentDownloadService, AgentUploadService agentUploadService) {
        this.agentUploadService = agentUploadService
        this.agentDownloadService = agentDownloadService
    }

}
