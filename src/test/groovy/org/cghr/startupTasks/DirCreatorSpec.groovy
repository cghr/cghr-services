package org.cghr.startupTasks

import spock.lang.Specification


/**
 * Created by ravitej on 25/4/14.
 */
class DirCreatorSpec extends Specification {

    DirCreator dirCreator
    String baseDir = File.createTempDir().absolutePath

    def setupSpec() {

    }

    def setup() {

        List dirs = [
                baseDir + File.separator + 'inbox',
                baseDir + File.separator + 'outbox',
                baseDir + File.separator + 'images/consent'

        ]

        dirCreator = new DirCreator(dirs)
    }

    def "should create non existing directories"() {

        when:
        dirCreator.create()

        then:
        new File(baseDir + File.separator + 'inbox').exists() == true
        new File(baseDir + File.separator + 'outbox').exists() == true
        new File(baseDir + File.separator + 'images/consent').exists() == true

    }

}