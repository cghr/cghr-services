package org.cghr.dataSync.providers

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class AgentProvider {

    AgentServiceProvider agentServiceProvider

    AgentProvider(AgentServiceProvider agentServiceProvider) {
        this.agentServiceProvider = agentServiceProvider
    }

    Agent downloadOrganizerAgent
    Agent downloadAgent
    Agent msgDistAgent
    Agent uploadAgent
    Agent fileUploadAgent

    List agents = [downloadOrganizerAgent, downloadAgent, msgDistAgent, uploadAgent, fileUploadAgent]

    List<Agent> provideAllAgents() {

        AgentService agentService = agentServiceProvider.provide()

        agents.collect {
            it.agentService = agentService
            it
        }
    }


}
