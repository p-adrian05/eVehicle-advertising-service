package org.example.core.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.storage.AdImageStorageService;
import org.example.core.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdImageStorageServiceImpl implements AdImageStorageService {

    @Value("${ad.default-image-root-location:images}")
    private Path rootLocation;

    private final StorageService storageService;

    @PostConstruct
    public void init() {
        try{
            Files.createDirectories(this.rootLocation);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private Path generateFolderName(){
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
        return Path.of(month+dayOfMonth);
    }

    @Override
    public Set<Path> store(MultipartFile[] imageFiles) throws FileUploadException {
        Objects.requireNonNull(imageFiles, "ImageFile array cannot be null");
        Path folderPath = this.rootLocation.resolve(generateFolderName());
        Set<Path> imagePaths = new HashSet<>();
        String imageName;

        for(MultipartFile multipartFile: imageFiles){
            imageName = generateAdImageName("jpg");
            imagePaths.add(storageService.store(multipartFile,folderPath,imageName));
        }
        return imagePaths;
    }
    @Override
    public void deleteImageByPath(String path) {
        Objects.requireNonNull(path, "Path cannot be null for deleting");
       storageService.deleteByPath(this.rootLocation.resolve(path));
    }

    @Override
    public Resource loadAdImage(String path) {
        Objects.requireNonNull(path, "Path cannot be null for loading resource");
        try{
            Resource resource;
            Path file = this.rootLocation.resolve(path);
            resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new ResourceAccessException("Could not read file"+path);
            }
        }catch (MalformedURLException e){
            throw new ResourceAccessException("Could not read file"+path,e);
        }

    }
    private String generateAdImageName(String extension) {
        Objects.requireNonNull(extension, "Extension cannot be null generating image name");
        return "ad" +
            UUID.randomUUID() +
            "." +
            extension;
    }


}
