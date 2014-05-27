package org.cghr.awakeFile
import org.awakefw.file.api.server.AwakeFileConfigurator

import java.sql.Connection
import java.sql.SQLException
/**
 * Created by ravitej on 8/5/14.
 */

class CghrAwakeFileConfigurator implements AwakeFileConfigurator {

    String userHome=System.getProperty('user.home')

    @Override
    File getServerRoot() {
        return new File(System.getProperty('user.home'))
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
