package org.cghr.dataSync.client

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

class UploadAgent implements Agent {


    AgentService agentService


    UploadAgent(AgentService agentService) {

        this.agentService = agentService

    }

    public void run() {

        List<Map> files = agentService.getOutboxFilesToUpload()
        uploadFiles(files)


    }

    void uploadFiles(List<Map> files) {

        files.each {
            fileInfo ->
                uploadFile(fileInfo)
                agentService.uploadSuccessful(fileInfo.id.toString())
        }
    }


    def uploadFile(Map fileInfo) {

        String filename = fileInfo.message
        File localFile = agentService.getOutboxFile(filename)
        agentService.upload(localFile)

    }

}
