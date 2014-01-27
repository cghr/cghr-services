package org.cghr.dataSync.commons

class SyncRunner {

    List agents = []

    //Run All agents who perform individual tasks which accomplish download and upload
    void run() {

        for (agentId in agents) {
            Agent agent = AgentProvider.provide(agentId)
            agent.run();
        }
    }
}
