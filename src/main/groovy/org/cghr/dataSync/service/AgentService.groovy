package org.cghr.dataSync.service

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 3/2/14.
 */
@TupleConstructor
class AgentService {

    @Delegate
    AgentDownloadService agentDownloadService

    @Delegate
    AgentUploadService agentUploadService

    @Delegate
    AgentMsgDistService agentMsgDistService

    @Delegate
    AgentFileUploadservice agentFileUploadservice

}
