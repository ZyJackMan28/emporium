package com.emporium.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String imageUpload(MultipartFile file);
}
