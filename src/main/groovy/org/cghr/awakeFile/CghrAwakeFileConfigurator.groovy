package org.cghr.awakeFile

import org.awakefw.file.api.server.AwakeFileConfigurator
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Connection
import java.sql.SQLException

/**
 * Created by ravitej on 8/5/14.
 */

class CghrAwakeFileConfigurator implements AwakeFileConfigurator {

    @Autowired
    String userHome

    @Override
    File getServerRoot() {
        return new File(userHome)
    }

    @Override
    boolean useOneRootPerUsername() {
        return false
    }

    @Override
    boolean allowCallAfterAnalysis(String s, Connection connection, String s2, List<Object> objects) throws IOException, SQLException {
        return false
    }

    @Override
    void runIfCallRefused(String s, Connection connection, String s2, String s3, List<Object> objects) throws IOException, SQLException {

    }
}
