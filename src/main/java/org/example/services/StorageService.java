package org.example.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;

public interface StorageService {

    void init();

    Path store(MultipartFile file,String folderName,String fileName) throws FileUploadException;
    Path store(MultipartFile file,String fileName) throws FileUploadException;
    void store(Map<String,MultipartFile> filesToSave, String folderName) throws FileUploadException;
    Resource loadAsResource(String filename,String path);
     String generateFolderName();
    Path loadPath(String fileName);

    void deleteByPath(String path);
}
