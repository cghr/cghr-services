package org.cghr.commons.web.controller

import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * Created by ravitej on 13/1/15.
 */
@RestController
@RequestMapping("/fileEntity/{filestore}")

class FileEntityService {

    @Autowired
    FileSystemStore fileSystemStore

    @RequestMapping(value = "", method = RequestMethod.POST)
    String saveOrUpdate(@PathVariable String filestore,
            @RequestParam("data") String jsonData,
            @RequestParam("file") MultipartFile file) {


        Map data = jsonData.jsonToMap()

        fileSystemStore.saveOrUpdate(data, filestore, file)
        fileSystemStore.createFileChangelogs(data, filestore)

        return 'uploaded successfully'
    }

}
