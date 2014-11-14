package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import org.cghr.dataSync.service.AgentService

/**
 * Created by ravitej on 14/11/14.
 */
@TupleConstructor
class AgentServiceProvider {


    AgentDownloadServiceProvider agentDownloadServiceProvider
    AgentFileUploadServiceProvider agentFileUploadserviceProvider
    AgentMsgDistServiceProvider agentMsgDistServiceProvider
    AgentUploadServiceProvider agentUploadServiceProvider

    AgentService provide() {

        def agentDownloadService = agentDownloadServiceProvider.provide()
        def agentUploadService = agentUploadServiceProvider.provide()
        def agentMsgDistService = agentMsgDistServiceProvider.provide()
        def agentFileUploadservice = agentFileUploadserviceProvider.provide()

        new AgentService(agentDownloadService, agentUploadService, agentMsgDistService, agentFileUploadservice)

    }

}
