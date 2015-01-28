package org.cghr.survey.controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/**
 * Created by ravitej on 21/9/14.
 */
@RestController
@RequestMapping("/survey/jsonSchemaList")
class JsonSchemaService {

    @Autowired
    String devJsonSchemaPath
    @Autowired
    String prodJsonSchemaPath

    @RequestMapping("/dev/{app}")
    List getAllSchemaNamesDev(@PathVariable("app") String app) {

        String path = devJsonSchemaPath.replaceAll("<appName>", app)
        getJsonSchemaFileNames(path)
    }

    @RequestMapping("/prod")
    List getAllSchemaNamesProduction() {
        getJsonSchemaFileNames(prodJsonSchemaPath)
    }

    List getJsonSchemaFileNames(String path) {

        List jsonSchemaDir = new File(path).listFiles()

        jsonSchemaDir
                .findAll { File file -> !file.isDirectory() }
                .collect { it.name }

    }
}
