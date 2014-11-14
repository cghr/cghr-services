package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import org.cghr.dataSync.client.DownloadAgent
import org.cghr.dataSync.client.DownloadOrganizerAgent
import org.cghr.dataSync.client.FileUploadAgent
import org.cghr.dataSync.client.UploadAgent
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.commons.MsgDistAgent
import org.cghr.dataSync.service.AgentService

@TupleConstructor(includes = ["agentServiceProvider"])
class AgentProvider {

    AgentServiceProvider agentServiceProvider

    //Order is very important
    List agents = [DownloadOrganizerAgent, DownloadAgent, MsgDistAgent, UploadAgent, FileUploadAgent]

    List<Agent> provideAllAgents() {

        AgentService agentService = agentServiceProvider.provide()
        agents.collect { it.newInstance(agentService) }
    }


}
