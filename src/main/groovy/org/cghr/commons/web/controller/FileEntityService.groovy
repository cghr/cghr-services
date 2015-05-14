package org.cghr.commons.web.controller

import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
/**
 * Created by ravitej on 13/1/15.
 */
@RestController
@RequestMapping("/fileEntity")

class FileEntityService {

    @Autowired
    FileSystemStore fileSystemStore

    @RequestMapping(value = "/{filestore}", method = RequestMethod.POST)
    Map saveOrUpdate(@PathVariable String filestore,
                     @RequestParam("data") String jsonData,
                     @RequestParam("file") MultipartFile file) {

        Map data = jsonData.jsonToMap()

        fileSystemStore.saveOrUpdate(data, filestore, file)
        fileSystemStore.createFileChangelogs(data, filestore)



        return [status: 'uploaded successfully']
    }

}
