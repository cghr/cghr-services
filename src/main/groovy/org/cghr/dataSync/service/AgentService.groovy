package org.cghr.dataSync.service
/**
 * Created by ravitej on 3/2/14.
 */
class AgentService {

    @Delegate
    AgentDownloadService agentDownloadService
    @Delegate
    AgentUploadService agentUploadService
    @Delegate
    AgentMsgDistService agentMsgDistService

    AgentService(AgentDownloadService agentDownloadService, AgentUploadService agentUploadService, AgentMsgDistService agentMsgDistService) {
        this.agentDownloadService = agentDownloadService
        this.agentUploadService = agentUploadService
        this.agentMsgDistService = agentMsgDistService
    }
}
