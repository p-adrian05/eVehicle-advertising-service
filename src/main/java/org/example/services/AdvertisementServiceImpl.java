package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.repository.util.AdState;
import org.example.repository.util.AdvertisementQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownCategoryException;
import org.example.exceptions.UnknownUserException;
import org.example.model.AdDetails;
import org.example.model.Image;
import org.example.model.Advertisement;
import org.example.repository.dao.AdvertisementDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService{

    private final AdvertisementDao advertisementDao;
    private final StorageService storageService;

    @Override
    public void createAdvertisement(Advertisement advertisement, AdDetails adDetails, MultipartFile[] imageFiles)
            throws UnknownUserException, UnknownCategoryException,FileUploadException {

        String folderName = "";
        Map<String,MultipartFile> filesToSave  = new HashMap<>();
        List<Image> imageModels = new LinkedList<>();
        String[] pathValues;

        for(Map.Entry<MultipartFile,Image> entry: createImageModels(imageFiles).entrySet()){
            pathValues = getFolderNameAndFilenameFromPath(entry.getValue().getPath());
            if(folderName.equals("")){
                folderName = pathValues[0];
            }
            imageModels.add(entry.getValue());
            filesToSave.put(pathValues[1],entry.getKey());
        }

        advertisement.setImages(imageModels);
        advertisementDao.createAdvertisement(advertisement,adDetails);
        storageService.store(filesToSave,folderName);
    }
    private String[] getFolderNameAndFilenameFromPath(String path){
        String[] pathArray = path.split("/");
        if(pathArray.length>=2){
            return new String[]{pathArray[pathArray.length-2],pathArray[pathArray.length-1]};
        }
        return new String[]{"",""};
    }

    @Override
    public void deleteAdvertisement(int id) throws UnknownAdvertisementException {
        advertisementDao.deleteAdvertisement(id);
    }
    private Map<MultipartFile,Image> createImageModels(MultipartFile[] imageFiles){
        String folderName = storageService.generateFolderName();
        return Arrays.stream(imageFiles).map(image-> Map.entry(image,Image.builder()
                .path("/"+folderName+"/"+generateAdImageName("jpg"))
                .uploadedTime(new Timestamp(new Date().getTime()))
                .build())).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void deleteImageFiles(List<Image> toDeletedImageModels){
       toDeletedImageModels.forEach(image -> storageService.deleteByPath("images/"+image.getPath()));
    }

    @Override
    public void updateAdvertisement(Advertisement advertisement) throws UnknownCategoryException, UnknownAdvertisementException {
        advertisementDao.updateAdvertisement(advertisement);
    }
    @Override
    public void changeState(int adId,AdState stateToChange,String creatorName) throws UnknownAdvertisementException {
        advertisementDao.changeState(adId,stateToChange,creatorName);
    }
    private String generateAdImageName(String extension){
        StringBuilder name = new StringBuilder();
        name.append("ad");
        name.append(UUID.randomUUID());
        name.append(".");
        name.append(extension);
        return name.toString();
    }

    @Override
    public void updateAllAdvertisement(Advertisement advertisement, AdDetails adDetails, MultipartFile[] imageFiles) throws UnknownCategoryException, UnknownAdvertisementException, UnknownUserException, FileUploadException {
        List<String> imageFileNames = Arrays.stream(imageFiles).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        List<Image> toDeleteImages =  advertisementDao.getAdvertisementById(advertisement.getId()).getImages().stream()
                .filter(image->!imageFileNames.contains(image.getPath())).collect(Collectors.toList());
       List<String> newImageNames = imageFileNames.stream().filter(name -> name.contains("newAdImage")).collect(Collectors.toList());

        List<Image> imageModels = new LinkedList<>();
        String folderName = "";
        String[] pathValues;
        Map<String,MultipartFile> filesToSave  = new HashMap<>();
        Map<MultipartFile,Image> newImageFileModelPairs = createImageModels(Arrays.stream(imageFiles)
                .filter(imageFile -> newImageNames.contains(imageFile.getOriginalFilename()))
                .toArray(MultipartFile[]::new));

        for(Map.Entry<MultipartFile,Image> entry: newImageFileModelPairs.entrySet()){
            pathValues = getFolderNameAndFilenameFromPath(entry.getValue().getPath());
            if(folderName.equals("")){
                folderName = pathValues[0];
            }
            imageModels.add(entry.getValue());
            filesToSave.put(pathValues[1],entry.getKey());
        }
        Arrays.stream(imageFiles)
                .filter(imageFile -> !newImageNames.contains(imageFile.getOriginalFilename()))
                .forEach(imageFile -> imageModels.add(
                        Image.builder()
                        .path(imageFile.getOriginalFilename())
                        .uploadedTime(new Timestamp(new Date().getTime()))
                        .build()));

        advertisement.setImages(imageModels);

        advertisementDao.updateAllAdvertisement(advertisement,adDetails);
        storageService.store(filesToSave,folderName);
        deleteImageFiles(toDeleteImages);
    }

    @Override
    public Advertisement getAdvertisementById(int id) throws UnknownAdvertisementException, UnknownUserException {
        return advertisementDao.getAdvertisementById(id);
    }

    @Override
    public AdDetails getAdDetailsById(int id) throws UnknownAdvertisementException {
        return advertisementDao.getAdDetailsById(id);
    }

    @Override
    public void updateAdDetails(AdDetails adDetails) throws UnknownAdvertisementException {
        advertisementDao.updateAdDetails(adDetails);
    }

    @Override
    public Slice<Advertisement> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return advertisementDao.getAdvertisements(params,pageable);
    }

    @Override
    public List<Advertisement> getSavedAdvertisementsByUsername(String username) throws UnknownUserException {
        return advertisementDao.getSavedAdvertisementsByUsername(username);
    }

    @Override
    public Page<Advertisement> getAdvertisementsByUsername(String username, Pageable pageable, AdState state) throws UnknownUserException {
        return advertisementDao.getAdvertisementsByUsername(username,pageable,state);
    }

    @Override
    public void addImages(int adId, List<Image> images) throws UnknownAdvertisementException {
        advertisementDao.addImages(adId,images);
    }

    @Override
    public void removeImages(int adId, List<String> imagePaths) throws UnknownAdvertisementException {
        advertisementDao.removeImages(adId,imagePaths);
    }
    @Override
    public String convertSortParamToValidForm(String sortParam) {
        Class<? extends Object> c = AdDetails.class;
        Field[] fields = c.getDeclaredFields();
        String[] classNames = AdDetails.class.getName().split("\\.");
        String className = classNames[classNames.length-1];
        className = String.valueOf(className.charAt(0)).toLowerCase() + className.substring(1);
        List<String> fieldsName = new LinkedList<>();
        for(Field field : fields ){
            fieldsName.add(field.getName());
        }
        if(fieldsName.contains(sortParam)){
            return className + "."+sortParam;
        }
        return sortParam;
    }

    @Override
    public List<String> getBrandNamesByCategory(String category) {
        return advertisementDao.getBrandNamesByCategory(category);
    }
    @Override
    public List<String> getCarTypesByBrandName(String category,String brandName) {
        return advertisementDao.getTypesByBrandName(category,brandName);
    }
    @Override
    public List<String> getCategories(){
        return advertisementDao.getCategories();
    }
}
