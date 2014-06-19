package org.cghr.dataSync.providers

import org.cghr.dataSync.client.DownloadAgent
import org.cghr.dataSync.client.DownloadOrganizerAgent
import org.cghr.dataSync.client.FileUploadAgent
import org.cghr.dataSync.client.UploadAgent
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.commons.MsgDistAgent
import org.cghr.dataSync.service.AgentService

class AgentProvider {

    AgentServiceProvider agentServiceProvider

    AgentProvider(AgentServiceProvider agentServiceProvider) {

        this.agentServiceProvider = agentServiceProvider
    }
    AgentService agentService

    //Agents
    Agent downloadOrganizerAgent
    Agent downloadAgent
    Agent msgDistAgent
    Agent uploadAgent
    Agent fileUploadAgent

    List<Agent> provideAllAgents() {

        createAgentsDynamically()
        return [downloadOrganizerAgent, downloadAgent, msgDistAgent, uploadAgent, fileUploadAgent]
    }

    void createAgentsDynamically() {
        this.agentService = agentServiceProvider.provide()

        downloadOrganizerAgent = new DownloadOrganizerAgent(agentService)
        downloadAgent = new DownloadAgent(agentService)
        msgDistAgent = new MsgDistAgent(agentService)
        uploadAgent = new UploadAgent(agentService)
        fileUploadAgent = new FileUploadAgent(agentService)
    }
}
