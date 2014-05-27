package org.cghr.dataSync.commons

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class SyncRunner {

    @Autowired
    AgentProvider agentProvider
    List<Agent> agents = []

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
