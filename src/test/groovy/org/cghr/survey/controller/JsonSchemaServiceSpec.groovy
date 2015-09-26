package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class JsonSchemaServiceSpec extends Specification {

    @Autowired
    JsonSchemaService jsonSchemaService

    MockMvc mockMvc

    def setupSpec() {

    }

    def setup() {

        File prodJsonSchemaDir = File.createTempDir()
        File devJsonSchemaDir = File.createTempDir()

        new File(prodJsonSchemaDir.absolutePath + "/file1.json").write('')
        new File(devJsonSchemaDir.absolutePath + "/hc/ui/src/assets/jsonSchema").mkdirs()
        new File(devJsonSchemaDir.absolutePath + "/hc/ui/src/assets/jsonSchema/file1.json").write('')
        new File(devJsonSchemaDir.absolutePath + "/hc/ui/src/assets/jsonSchema/file2.json").write('')

        jsonSchemaService.prodJsonSchemaPath = prodJsonSchemaDir.absolutePath
        jsonSchemaService.devJsonSchemaPath = devJsonSchemaDir.absolutePath + "/<appName>" + "/ui/src/assets/jsonSchema"

        mockMvc = MockMvcBuilders.standaloneSetup(jsonSchemaService).build()
    }


    def "should get list of json files in development"() {
        expect:
        mockMvc.perform(get("/survey/jsonSchemaList/dev/hc"))
                .andExpect(status().isOk())
                .andExpect(content().string('["file2.json","file1.json"]'))

    }

    def "should get list of json files in production"() {
        expect:
        mockMvc.perform(get("/survey/jsonSchemaList/prod"))
                .andExpect(status().isOk())
                .andExpect(content().string('["file1.json"]'))

    }


}