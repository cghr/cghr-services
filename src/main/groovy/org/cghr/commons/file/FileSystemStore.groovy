package org.cghr.commons.file

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


    FileSystemStore(Map fileStoreFactory, DbStore dbStore) {
        this.fileStoreFactory = fileStoreFactory
        this.dbStore = dbStore
    }

    void saveOrUpdate(Map formData, String fileStore, MultipartFile file,String rootPath) {


        Map data = formData
        String fileName = data.remove("filename")
        String fileId=data.remove('fileId')
        fileName=fileName+'.'+file.name.split("\\.")[1]
        String fullPath = rootPath+File.separator+ ((Map)fileStoreFactory.get(fileStore)).get(fileId)

        //Save file to Disk
        byte[] bytes = file.getBytes()
        String fileContent = new String(bytes, "UTF-8")
        File newFile = getNewFile( fullPath, fileName)
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


}
