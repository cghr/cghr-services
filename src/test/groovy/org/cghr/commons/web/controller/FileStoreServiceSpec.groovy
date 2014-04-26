package org.cghr.commons.web.controller

import org.cghr.commons.file.FileSystemStore
import spock.lang.Specification

/**
 * Created by ravitej on 25/4/14.
 */
class FileStoreServiceSpec extends Specification {


    FileStoreService fileStoreService

    def setupSpec() {

    }

    def setup() {
        FileSystemStore fileSystemStore = Stub() {

        }
        fileStoreService = new FileStoreService(fileSystemStore)

    }

    //Todo
    def "should save the data and write consent file to appropriate path"() {

    }

}