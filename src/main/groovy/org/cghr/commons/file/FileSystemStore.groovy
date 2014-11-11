package org.cghr.commons.file

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbStore
import org.springframework.web.multipart.MultipartFile

/**
 * Created by ravitej on 24/4/14.
 */
@TupleConstructor
class FileSystemStore {


    Map fileStoreFactory
    DbStore dbStore

    void saveOrUpdate(Map formData, String fileStore, MultipartFile file) {

        Map data = formData.subMap(formData.keySet().toList() - ['filename', 'category'])

        def fileName = formData.filename
        def category = formData.category

        String filePath = (fileStoreFactory."$fileStore")."$category"
        File newFile = new File(filePath + '/' + fileName)
        file.transferTo(newFile)

        dbStore.saveOrUpdate(data, fileStore)

    }


    void createFileChangelogs(final Map fileData, final String filestore) {

        def (data, fileMetadata) = getDataAndFileMetaData(fileData, filestore)

        dbStore.saveOrUpdate(fileMetadata, 'filechangelog')
        dbStore.createDataChangeLogs(data, filestore)
    }

    def getDataAndFileMetaData(Map fileData, String filestore) {

        Map data = fileData.subMap(fileData.keySet() - ['filename', 'category'])
        Map fileMetadata = [filename: fileData.filename, filestore: filestore, category: fileData.category]

        return [data, fileMetadata]
    }


}
