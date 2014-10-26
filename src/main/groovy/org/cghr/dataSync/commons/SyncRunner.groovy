package org.cghr.dataSync.commons

import groovy.transform.TupleConstructor
import org.cghr.dataSync.providers.AgentProvider

@TupleConstructor
class SyncRunner {

    AgentProvider agentProvider

    void run() {
        List<Agent> agents = agentProvider.provideAllAgents()
        agents.each { it.run() }
    }
}
