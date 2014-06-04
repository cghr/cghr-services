package org.cghr.commons.file

import com.google.gson.Gson
import org.apache.commons.fileupload.FileItem
import org.cghr.commons.db.DbStore

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
    Gson gson = new Gson()

    void saveOrUpdate(Map formData, String fileStore, FileItem file) {

        Map data = (HashMap) formData.clone()

        String fileName = data.remove("filename")
        String category = data.remove('category')

        String fullPath = ((Map) fileStoreFactory.get(fileStore)).get(category)

        //Save file to Disk
        File newFile = getNewFile(fullPath, fileName)
        file.write(newFile)

        //Save data to Database
        dbStore.saveOrUpdate(data, fileStore)

    }

    File getNewFile(String dirPath, String fileName) {
        File dir = new File(dirPath)
        if (!dir.exists())
            dir.mkdirs()

        File file = new File(dir.getAbsolutePath()
                + '/' + fileName)
        file.write('')
        return file
    }

    void createFileChangelogs(Map fileData, String filestore) {

        Map data = (HashMap) fileData.clone()
        Map fileMetadata = [filename: data.remove('filename'), filestore: filestore, fileId: data.remove('fileId')]

        dbStore.saveOrUpdate(fileMetadata, 'filechangelog')
        dbStore.createDataChangeLogs(data, filestore)

    }


}
