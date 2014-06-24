package org.cghr.dataSync.client

import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

/**
 * Created by ravitej on 8/5/14.
 */
class FileUploadAgent implements Agent {

    AgentService agentService

    FileUploadAgent(AgentService agentService) {
        this.agentService = agentService
    }

    @Override
    void run() {

        List<Map> files = agentService.getFileChangelogs()
        uploadFiles(files)
    }

    void uploadFiles(List<Map> files) {

        files.each {
            try {
                agentService.uploadFile(it)
                agentService.fileUploadSuccessful(it.id)
            }
            catch (Exception e) {
                e.printStackTrace();
                println 'error  uploading the file'
            }


        }
    }
}
