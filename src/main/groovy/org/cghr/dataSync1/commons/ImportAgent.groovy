package org.cghr.dataSync1.commons
import com.google.gson.Gson
import org.cghr.dataSync1.service.AgentService

class ImportAgent implements Agent {

    AgentService agentService
    Gson gson=new Gson()


    ImportAgent(AgentService agentService) {
        this.agentService = agentService


    }


    public void run() {

        List<Map> files = agentService.getFilesToImport()
        importFiles(files)

    }

    void importFiles(List<Map> files) {

        files.each {
            fileInfo ->
                importFile(fileInfo.message)
        }

    }

    void importFile(String file) {
        String fileContent = agentService.getInboxFileContents(file)

        List<Map> items = gson.fromJson(fileContent, List.class)
        items.each {
            item ->
                agentService.saveLogInfToDatabase(item)
        }

    }


}
