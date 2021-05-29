package org.example.core.storage;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService2 {

    public Path store(MultipartFile file,Path folderPath,String fileName) throws FileUploadException;

    void deleteByPath(Path path);

    Resource loadAsResource(String filename, String path);
}
