package org.cghr.awakefile

import org.cghr.awakeFile.CghrAwakeFileConfigurator
import spock.lang.Specification


/**
 * Created by ravitej on 12/11/14.
 */
class CghrAwakeFileconfiguratorSpec extends Specification {

    CghrAwakeFileConfigurator cghrAwakeFileConfigurator

    String tmpDir = File.createTempDir().absolutePath


    def setup() {

        System.setProperty("user.home", tmpDir)
        cghrAwakeFileConfigurator = new CghrAwakeFileConfigurator()
    }

    def "should get the user.home from System properties"() {

        expect:
        cghrAwakeFileConfigurator.getServerRoot().absolutePath == tmpDir

    }

    def "should return false for for useOneRoot per username"() {

        expect:
        cghrAwakeFileConfigurator.useOneRootPerUsername() == false

    }


}