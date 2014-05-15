package org.cghr.commons.web.controller

import com.google.gson.Gson
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileItemFactory
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.cghr.commons.file.FileSystemStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/file/fileStoreService")
class FileStoreService {

    @Autowired
    FileSystemStore fileSystemStore

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    //String saveOrUpdate(@RequestParam("file") MultipartFile file, @RequestParam("data") String json) {
    String saveOrUpdate(HttpServletRequest request) {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request)


        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                Map data = [:]
                MultipartFile multipartFile
                FileItem image
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();

                    if (!item.isFormField()) {
                        image = item
                    } else {
                        data = new Gson().fromJson(item.getString(), Map.class)
                    }

                }
                String filestore = data.remove('filestore')
                fileSystemStore.saveOrUpdate(data, filestore, image)
                fileSystemStore.createFileChangelogs(data, filestore)
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        Map data = new Gson().fromJson(json, Map.class)
//        String filestore = data.remove('filestore')
//        fileSystemStore.saveOrUpdate(data, filestore, file)
//
//        //Create changelogs for file and data
//        fileSystemStore.createFileChangelogs(data, filestore)
        return ''


    }


}
