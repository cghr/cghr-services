package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 12/8/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyContextLoader.class)
class FileStoreServiceSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    FileStoreService fileStoreService
    Map fileStoreFactory = [memberImage: [memberConsent: 'dummyPath/repo/images/consent/', memberPhoto: 'hcDemo/images/photo/']]
    MockMvc mockMvc
    @Autowired
    DbTester dbTester
    @Shared
    def dataSet

    def setupSpec() {
        dataSet = new MockData().getFilteredSampleData('filechangelog', ['category', 'filestore', 'filename', 'status'])
    }

    def setup() {

        dbTester.clean('memberImage,datachangelog,filechangelog')
        mockMvc = MockMvcBuilders.standaloneSetup(fileStoreService).build()

    }


    def "should save the data and write consent file to appropriate path"() {
        given:
        Map formData = [memberId: '151001001', consent: '151001001_consent.png', filename: '151001001_consent.png', category: 'memberConsent', filestore: 'memberImage']
        String fileStore = 'memberImage'
        byte[] content = "dummy File contents".getBytes()

        when:
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/file/fileStoreService")
                .file("file", content)
                .param("data", new Gson().toJson(formData)))


        then:
        File dir = new File(fileStoreFactory.get(fileStore).get('memberConsent'))
        dir.listFiles().length == 1
        gSql.rows('select * from memberImage').size() == 1
        gSql.firstRow('select category,filestore,filename,status from filechangelog') == dataSet[0]


    }


}