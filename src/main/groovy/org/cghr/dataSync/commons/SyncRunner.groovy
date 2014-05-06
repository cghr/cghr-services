package org.cghr.dataSync.commons

import org.springframework.beans.factory.annotation.Autowired

class SyncRunner {

    @Autowired
    AgentProvider agentProvider
    List agents = []

    SyncRunner(AgentProvider agentProvider) {
        this.agentProvider = agentProvider
    }

    void run() {
        agents = agentProvider.provideAllAgents()
        agents.each {
            Agent agent ->
                agent.run()
        }
    }
}
