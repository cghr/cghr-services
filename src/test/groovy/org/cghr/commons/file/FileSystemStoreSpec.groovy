package org.cghr.commons.file
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification
/**
 * Created by ravitej on 24/4/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class FileSystemStoreSpec extends Specification {

    FileSystemStore fileSystemStore

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt


    @Autowired
    DbStore dbStore


    String userHome = File.createTempDir().absolutePath + '/'

    Map fileStoreFactory = [memberImage: [memberConsent: userHome + 'hcDemo/images/consent/', memberPhoto: userHome + 'hcDemo/images/photo/']]

    def setupSpec() {

    }

    def setup() {

        new File(userHome+'hcDemo/images/consent').mkdirs()
        new File(userHome+'hcDemo/images/photo').mkdirs()

        fileSystemStore = new FileSystemStore(fileStoreFactory, dbStore)

        dt.clean('memberImage')
        dt.clean('datachangelog')
    }




    def "should save the data and write consent file to appropriate path"() {

        given:
        Map formData = [memberId: '151001001', consent: '151001001_consent.png', filename: '151001001_consent', category: 'memberConsent']
        String fileStore = 'memberImage'
        byte[] content = "dummy File contents".getBytes()
        MockMultipartFile multipartFile = new MockMultipartFile("151001001_consent.png", 'fileData', "text/plain", content);


        when:
        fileSystemStore.saveOrUpdate(formData, fileStore, multipartFile)

        then:
        File dir = new File(fileStoreFactory.get(fileStore).get('memberConsent'))
        dir.listFiles().length == 1
        gSql.rows('select * from memberImage').size() == 1


    }


    def "should save the data and write the photo file to appropriate path"() {

        given:
        Map formData = [memberId: '151001001', photo: '151001001_photo.png', filename: '151001001_photo', category: 'memberPhoto']
        String fileStore = 'memberImage'
        byte[] content = "dummy File contents".getBytes()
        MockMultipartFile multipartFile = new MockMultipartFile("151001001_photo.png", 'fileData', "text/plain", content);

        when:
        fileSystemStore.saveOrUpdate(formData, fileStore, multipartFile)

        then:
        File dir = new File(fileStoreFactory.get(fileStore).get('memberPhoto'))
        dir.listFiles().length == 1
        gSql.rows('select * from memberImage').size() == 1


    }

}