package org.cghr.commons.web.controller

import groovy.transform.CompileStatic
import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest

@CompileStatic
@Controller
@RequestMapping("/file/fileStoreService")
class FileStoreService {

    @Autowired
    FileSystemStore fileSystemStore
    @Autowired
    String basePath


    FileStoreService(FileSystemStore fileSystemStore, String basePath) {
        this.fileSystemStore = fileSystemStore
        this.basePath = basePath
    }

    FileStoreService() {
        
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    void saveOrUpdate(
            @RequestParam("data") Map data, @RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String fileStore = data.remove("filestore")
        fileSystemStore.saveOrUpdate(data, fileStore, file, basePath)


    }


}
