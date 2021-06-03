package org.example.core.image;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.image.persistence.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface AdImageService {

    Set<ImageEntity> store(MultipartFile[] imageFiles) throws FileUploadException;

    Set<ImageEntity> updateAndStore(Set<ImageEntity> currentImages, MultipartFile[] imageFiles)
        throws FileUploadException;
}
