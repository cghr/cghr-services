package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 21/9/14.
 */
@RestController
@RequestMapping("/JsonSchemaService")
class JsonSchemaService {

    @Autowired
    String devJsonSchemaPath
    @Autowired
    String prodJsonSchemaPath

    @RequestMapping(value = "/dev/{app}", method = RequestMethod.GET, produces = "application/json")
    String getAllSchemaNamesDev(@PathVariable("app") String app) {

        String path = devJsonSchemaPath.replaceAll("<appName>", app)
        getJsonSchemaFileNames(path).toJson()
    }

    @RequestMapping(value = "/prod", method = RequestMethod.GET, produces = "application/json")
    String getAllSchemaNamesProduction() {
        getJsonSchemaFileNames(prodJsonSchemaPath).toJson()
    }


    List getJsonSchemaFileNames(String path) {
        List jsonSchemaDir = new File(path).listFiles()

        jsonSchemaDir
                .findAll { File file -> !file.isDirectory() }
                .collect { it.name }

    }
}
