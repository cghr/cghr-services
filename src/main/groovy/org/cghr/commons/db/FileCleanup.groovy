package org.cghr.commons.db

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 28/6/15.
 */
@TupleConstructor
class FileCleanup {


    HashMap fileStoreFactory
    DbAccess dbAccess

    void cleanupFiles() {

        if (!hasFileChangelogs()) {

            List folders = fileStoreFactory.memberImage
            List folderPaths = folders.collect { key, folderPath -> folderPath }
            deleteFolders(folderPaths)

        }
    }

    boolean hasFileChangelogs() {

        int count = dbAccess.firstRow("select count(*) logs from filechangelog where status is null").filechangelog
        count > 0
    }

    void deleteFolders(List folderPaths) {
        folderPaths.each { deleteFolder(it) }

    }

    void deleteFolder(String folderPath) {

        new File(folderPath).deleteDir()

    }


}
