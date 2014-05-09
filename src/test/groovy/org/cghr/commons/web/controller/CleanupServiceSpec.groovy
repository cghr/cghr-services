package org.cghr.commons.web.controller
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 9/5/14.
 */

@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class CleanupServiceSpec extends Specification {

    @Autowired
    CleanupService cleanupService
    @Autowired
    Sql gSql
    MockMvc mockMvc
    @Autowired
    DbTester dbTester


    def setupSpec() {

    }

    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(cleanupService).build()
        dbTester.cleanInsert('country,user,authtoken,userlog,inbox,outbox,datachangelog,filechangelog,memberImage,sales')

    }

    def "should cleanup all tables except the excluded entities"() {

        when:
        mockMvc.perform(get('/data/cleanup'))
                .andExpect(status().isOk())

        then:
        gSql.rows('select * from user').size() == 5
        gSql.rows('select * from sales').size() == 0
        gSql.rows('select * from country').size() == 0


    }

}