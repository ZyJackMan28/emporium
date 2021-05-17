package com.emporium.fileupload.web;

import com.emporium.fileupload.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("image")
    public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile file){
        //逻辑就是根据上传的文件，获取上传后的地址
        //http://images.emporium.com/group1/M00/00/04/wKgBaWBlXw6Ac24aAEfazrA3W00584.png
        //http://192.168.1.105/group1/M00/00/04/wKgBaWBlXw6Ac24aAEfazrA3W00584.png
       String fileAddress =  fileUploadService.imageUpload(file);
       return ResponseEntity.ok(fileAddress);
    }
}
