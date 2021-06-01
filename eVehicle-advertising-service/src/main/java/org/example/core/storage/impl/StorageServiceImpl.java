package org.example.core.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Override
    public Path store(MultipartFile file,Path folderPath,String fileName) throws FileUploadException {
        Objects.requireNonNull(file, "MultipartFile cannot be null for storing");
        Objects.requireNonNull(folderPath, "FolderPath cannot be null for storing");
        Objects.requireNonNull(fileName, "Filename cannot be null for storing");
        Path path = folderPath.resolve(fileName);
        try {
            Files.createDirectories(folderPath);
            Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
       throw new FileUploadException(String.format("Failed to upload file: %s", file));
    }

    @Override
    public Resource loadAsResource(String filename,String path) {
        Objects.requireNonNull(path, "Path cannot be null for loading resource");
        Objects.requireNonNull(filename, "Filename cannot be null for loading resource");
        try{
            Resource resource;
            Path file = Path.of(path).resolve(filename);
            resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new ResourceAccessException("Could not read file"+filename);
            }
        }catch (MalformedURLException e){
            throw new ResourceAccessException("Could not read file"+filename,e);
        }

    }

    @Override
    public void deleteByPath(Path path) {
        Objects.requireNonNull(path, "Path cannot be null for deleting");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ResourceAccessException("Could not delete file"+path,e);
        }
    }
}
