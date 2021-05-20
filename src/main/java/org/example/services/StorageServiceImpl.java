package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService{

    private final Path rootLocation;

    public StorageServiceImpl(){
        this.rootLocation = Paths.get("images");
        init();
    }

    @Override
    public void init() {
        try{
            Files.createDirectories(this.rootLocation);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public Path store(MultipartFile file,String folderName,String fileName) throws FileUploadException {
        Path path = this.rootLocation.resolve(folderName).resolve(fileName);
        try {
            Files.createDirectories(Path.of(String.valueOf(this.rootLocation.resolve(folderName))));
            Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            return this.rootLocation.resolve(folderName);
        } catch (IOException e) {
            e.printStackTrace();
        }
       throw new FileUploadException(String.format("Failed to upload file: %s",file.getOriginalFilename()));
    }

    @Override
    public Path store(MultipartFile file,String fileName) throws FileUploadException {
        String folderName = generateFolderName();
        return store(file,folderName,fileName);
    }
    @Override
    public void store(Map<String,MultipartFile> filesToSave,String folderName) throws FileUploadException {
        for(Map.Entry<String,MultipartFile> entry:filesToSave.entrySet()){
            store(entry.getValue(),folderName,entry.getKey());
        }
    }
    @Override
    public String generateFolderName(){
        LocalDate date = LocalDate.now();
        String month = "";
        String dayOfMonth = "";
        if(date.getMonth().getValue()<10){
            month = "0"+date.getMonth().getValue();
        }else{
            month = String.valueOf(date.getMonth().getValue());
        }
        if(date.getDayOfMonth()<10){
            dayOfMonth = "0"+date.getDayOfMonth();
        }else{
            dayOfMonth = String.valueOf(date.getDayOfMonth());
        }
        return month+dayOfMonth;
    }

    @Override
    public Resource loadAsResource(String filename,String path) {
        try{
            Resource resource;
            Path file = rootLocation.resolve(path).resolve(filename);
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
    public Path loadPath(String fileName) {
        return rootLocation.resolve(fileName);
    }

    @Override
    public void deleteByPath(String path) {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            throw new ResourceAccessException("Could not delete file"+path,e);
        }
    }
}
