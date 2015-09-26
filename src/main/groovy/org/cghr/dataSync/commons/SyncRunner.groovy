package org.cghr.dataSync.commons

import groovy.transform.TupleConstructor
import org.cghr.dataSync.providers.AgentProvider

@TupleConstructor
class SyncRunner {

    AgentProvider agentProvider

    void run(role) {

        List<Agent> agents = agentProvider.provideAllAgents()
        List<Agent> applicableAgents = (role == 'manager') ? agents.take((agents.size()) - 1) : agents

        applicableAgents.each { it.run() }
    }
}
