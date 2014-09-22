package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
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

    JsonSchemaService() {

    }

    JsonSchemaService(devJsonSchemaPath, prodJsonSchemaPath) {
        this.devJsonSchemaPath = devJsonSchemaPath
        this.prodJsonSchemaPath = prodJsonSchemaPath
    }


    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    String getAllSchemaNamesProduction() {
        getJsonSchemaFileNames(prodJsonSchemaPath).toJson()
    }

    @RequestMapping(value = "/{app}", method = RequestMethod.GET, produces = "application/json")
    String getAllSchemaNamesDev(@RequestParam("app") String app) {

        String path = devJsonSchemaPath.replaceAll("<appName>", app)
        getJsonSchemaFileNames(path).toJson()
    }

    List getJsonSchemaFileNames(String path) {
        List jsonSchemaDir = new File(path).listFiles()
        List fileNames = jsonSchemaDir.collect {
            it.name
        }
        fileNames
    }

}
