package org.cghr.awakeFile

import org.awakefw.file.api.server.AwakeFileConfigurator

import java.sql.Connection
import java.sql.SQLException

/**
 * Created by ravitej on 8/5/14.
 */
class CghrAwakeFileConfigurator implements AwakeFileConfigurator {

    @Override
    File getServerRoot() {
        new File(System.getProperty('basePath'))
    }

    @Override
    boolean useOneRootPerUsername() {
        false
    }

    @Override
    boolean allowCallAfterAnalysis(String s, Connection connection, String s2, List<Object> objects) throws IOException, SQLException {
        false
    }

    @Override
    void runIfCallRefused(String s, Connection connection, String s2, String s3, List<Object> objects) throws IOException, SQLException {

    }
}
