package org.example.core.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.storage.AdImageStorageService;
import org.example.core.storage.StorageService2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdImageStorageServiceImpl implements AdImageStorageService {

    @Value("${ad.default-image-root-location:images}")
    private Path rootLocation;

    private final StorageService2 storageService;

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
        Path folderPath = this.rootLocation.resolve(generateFolderName());
        Set<Path> imagePaths = new HashSet<>();
        String imageName;

        for(MultipartFile multipartFile: imageFiles){
            imageName = generateAdImageName("jpg");
            imagePaths.add(storageService.store(multipartFile,folderPath,imageName));
        }
        log.info(imagePaths.toString());
        return imagePaths;
    }

    @Override
    public Path getRootPath() {
        return rootLocation;
    }

    @Override
    public void deletePaths(Set<Path> paths) {
        paths.forEach(storageService::deleteByPath);
    }

    private String generateAdImageName(String extension) {
        StringBuilder name = new StringBuilder();
        name.append("ad");
        name.append(UUID.randomUUID());
        name.append(".");
        name.append(extension);
        return name.toString();
    }


}
