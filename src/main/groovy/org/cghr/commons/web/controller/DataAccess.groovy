package org.cghr.commons.web.controller

import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data/dataAccessService")
class DataAccess {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "/{dataStore}/{keyField}/{keyFieldValue}", method = RequestMethod.GET, produces = "application/json")
    String getDataAsJson(
            @PathVariable final String dataStore, @PathVariable final String keyField, @PathVariable final String keyFieldValue) {

        dbAccess.firstRow(dataStore, keyField, keyFieldValue).toJson()
    }

}
