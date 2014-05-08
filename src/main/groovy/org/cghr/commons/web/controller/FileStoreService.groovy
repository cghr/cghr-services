package org.cghr.commons.web.controller
import com.google.gson.Gson
import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file/fileStoreService")
class FileStoreService {

    @Autowired
    FileSystemStore fileSystemStore

    @RequestMapping("")
    String saveOrUpdate(@RequestParam("file") MultipartFile file, @RequestParam("data") String json) {

        Map data = new Gson().fromJson(json, Map.class)
        String filestore = data.remove('filestore')
        fileSystemStore.saveOrUpdate(data, filestore, file)

        //Create changelogs for file and data
        fileSystemStore.createFileChangelogs(data,filestore)


    }


}
