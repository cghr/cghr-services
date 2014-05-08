package org.cghr.commons.file

import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbStore
import org.springframework.web.multipart.MultipartFile

/**
 * Created by ravitej on 24/4/14.
 */
@CompileStatic
class FileSystemStore {


    Map fileStoreFactory
    DbStore dbStore
    String userHome
    Gson gson=new Gson()


    FileSystemStore(Map fileStoreFactory, DbStore dbStore, String userHome) {
        this.fileStoreFactory = fileStoreFactory
        this.dbStore = dbStore
        this.userHome = userHome
    }

    void saveOrUpdate(Map formData, String fileStore, MultipartFile file) {


        Map data = gson.fromJson(gson.toJson(formData),Map.class)
        String fileName = data.remove("filename")
        String fileId = data.remove('fileId')
        //fileName=fileName+'.'+file.name.split("\\.")[1]
        fileName = fileName + '.png'
        String fullPath = userHome + File.separator + ((Map) fileStoreFactory.get(fileStore)).get(fileId)

        //Save file to Disk
        byte[] bytes = file.getBytes()
        String fileContent = new String(bytes, "UTF-8")
        File newFile = getNewFile(fullPath, fileName)
        newFile.setText(fileContent)

        //Save data to Database
        dbStore.saveOrUpdate(data, fileStore)

    }

    File getNewFile(String dirPath, String fileName) {
        File dir = new File(dirPath)
        if (!dir.exists())
            dir.mkdirs()

        File file = new File(dir.getAbsolutePath()
                + File.separator + fileName)
        file.write('')
        return file
    }

    void createFileChangelogs(Map fileData, String filestore) {

        Map data = gson.fromJson(gson.toJson(fileData),Map.class)
        println 'data '+data
        Map fileMetadata = [filename: data.remove('filename'), filestore: filestore, fileId: data.remove('fileId')]

        dbStore.saveOrUpdate(fileMetadata,'filechangelog')
        dbStore.createDataChangeLogs(data, filestore)


    }


}
