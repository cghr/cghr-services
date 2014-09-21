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

//    void saveOrUpdateOld(Map formData, String fileStore, FileItem file) {
//
//        Map data = formData.subMap(formData.keySet().toList() - ['filename', 'category'])
//
//        def fileName = formData.filename
//        def category = formData.category
//
//        String fullPath = (fileStoreFactory."$fileStore")."$category"
//
//        //Save file to Disk
//        File newFile = getNewFile(fullPath, fileName)
//        file.write(newFile)
//
//        //Save data to Database
//        dbStore.saveOrUpdate(data, fileStore)
//
//    }

    void saveOrUpdate(Map formData, String fileStore, MultipartFile file) {

        Map data = formData.subMap(formData.keySet().toList() - ['filename', 'category'])

        def fileName = formData.filename
        def category = formData.category
        println 'filestore '+fileStore
        println 'category '+category
        println fileStoreFactory

        String filePath = (fileStoreFactory."$fileStore")."$category"

        //Save file to Disk

        File newFile = getNewFile(filePath, fileName)
        file.transferTo(newFile)

        //Save data to Database
        dbStore.saveOrUpdate(data, fileStore)

    }

    File getNewFile(String dirPath, String fileName) {

        println 'gettting new file '
        println 'dir path '+dirPath
        println 'filename '+fileName

        File dir = new File(dirPath)
        File file = new File(dir.getAbsolutePath()
                + '/' + fileName)
        file.write('')
        return file
    }

    void createFileChangelogs(Map fileData, String filestore) {

        Map data = (HashMap) fileData.clone()
        Map fileMetadata = [filename: data.remove('filename'), filestore: filestore, category: data.remove('category')]

        dbStore.saveOrUpdate(fileMetadata, 'filechangelog')
        dbStore.createDataChangeLogs(data, filestore)

    }


}
