package org.cghr.dataSync.commons

import org.springframework.beans.factory.annotation.Autowired

class SyncRunner {

    @Autowired
    AgentProvider agentProvider


    SyncRunner(AgentProvider agentProvider) {
        this.agentProvider = agentProvider
    }

    void run() {
        List<Agent> agents = agentProvider.provideAllAgents()
        agents.each { it.run() }
    }
}
