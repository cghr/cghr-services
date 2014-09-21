package org.cghr.commons.web.controller

import com.google.gson.Gson
import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Created by ravitej on 12/8/14.
 */

@RestController
@RequestMapping("/file/fileStoreService")
class FileStoreService {

    @Autowired
    FileSystemStore fileSystemStore

    @RequestMapping(value = "", method = RequestMethod.POST)
    String saveOrUpdate(@RequestParam(value="data",required = false) String jsonData, @RequestParam(value="file",required =false) MultipartFile file) {


        println 'data '+jsonData
        println file

        Map data = new Gson().fromJson(jsonData, HashMap)
        String filestore = data.remove('filestore')

        fileSystemStore.saveOrUpdate(data, filestore, file)
        fileSystemStore.createFileChangelogs(data, filestore)

        return ''
    }


}
