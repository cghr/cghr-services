package org.cghr.survey.controller
import com.google.gson.Gson
import org.cghr.GenericGroovyContextLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class JsonSchemaServiceSpec extends Specification {

    @Autowired
    JsonSchemaService jsonSchemaService

    MockMvc mockMvc

    def setupSpec() {

    }

    def setup() {

        File jsonSchemaDir = File.createTempDir()
        new File(jsonSchemaDir.absolutePath + "/file1.json").write('')
        jsonSchemaService.prodJsonSchemaPath = jsonSchemaDir.absolutePath
        mockMvc = MockMvcBuilders.standaloneSetup(jsonSchemaService).build()
    }

    def "should get a value for a given lookup  data"() {
        given:
        Map lookup = [entity: 'country', field: 'name', ref: 'continent', refId: 'asia']
        String json = new Gson().toJson(lookup)
        expect:
        mockMvc.perform(get("/JsonSchemaService"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('["file1.json"]'))

    }


}