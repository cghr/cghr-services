package org.cghr.commons.file

import org.cghr.commons.db.DbStore
import org.springframework.web.multipart.MultipartFile

/**
 * Created by ravitej on 24/4/14.
 */
class FileSystemStore {


    Map fileStoreFactory
    DbStore dbStore

    FileSystemStore(Map fileStoreFactory, DbStore dbStore) {
        this.fileStoreFactory = fileStoreFactory
        this.dbStore = dbStore
    }


    void saveOrUpdate(Map formData, String fileStore, MultipartFile file) {

        Map data = formData.subMap(formData.keySet().toList() - ['filename', 'category'])

        def fileName = formData.filename
        def category = formData.category

        String filePath = (fileStoreFactory."$fileStore")."$category"

        //Save file to Disk

        File newFile = getNewFile(filePath, fileName)
        file.transferTo(newFile)

        //Save data to Database
        dbStore.saveOrUpdate(data, fileStore)

    }

    File getNewFile(String dirPath, String fileName) {

        File file = new File(dirPath + '/' + fileName)
        file.write('')
        file
    }

    void createFileChangelogs(Map fileData, String filestore) {

        Map data = (HashMap) fileData.clone()
        Map fileMetadata = [filename: data.remove('filename'), filestore: filestore, category: data.remove('category')]

        dbStore.saveOrUpdate(fileMetadata, 'filechangelog')
        dbStore.createDataChangeLogs(data, filestore)

    }


}
