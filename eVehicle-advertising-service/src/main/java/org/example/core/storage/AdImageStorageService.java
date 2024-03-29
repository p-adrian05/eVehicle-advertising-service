package org.example.core.storage;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Set;

public interface AdImageStorageService {

    Set<Path> store(MultipartFile[] imageFiles) throws FileUploadException;

    void deleteImageByPath(String path);

    Resource loadAdImage(String path);
}
