package org.cghr.commons.web.controller

import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/data/dataStoreBatchService")
class DataStoreBatch {

    @Autowired
    DbStore dbStore
    @Autowired
    String serverBaseUrl


    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    void saveData(@RequestBody final Map[] data, HttpServletRequest request) {

        List changelogs = data.toList()
        dbStore.saveOrUpdateBatch(changelogs)

        String requestHost = getRequestHost(request)
        if (isNotSeverHost(requestHost))
            generateChangelogs(changelogs)

    }

    void generateChangelogs(List changelogs) {
        changelogs.each {
            dbStore.createDataChangeLogs(it.data, it.datastore)
        }
    }

    String getRequestHost(HttpServletRequest request) {
        request.getRequestURL().toURL().getHost()
    }

    String getServerHost() {
        serverBaseUrl.toURL().getHost()
    }

    boolean isNotSeverHost(String requestHost) {
        !(requestHost == serverHost && requestHost != 'localhost')
    }


}
