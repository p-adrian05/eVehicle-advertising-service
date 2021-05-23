package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.advertisement.AdvertisementAllDataDto;
import org.example.controller.dto.advertisement.AdvertisementDetailsDto;
import org.example.controller.dto.advertisement.AdvertisementDto;
import org.example.controller.dto.advertisement.CreateAdvertisementDto;
import org.example.controller.dto.advertisement.SavedAdDto;
import org.example.controller.dto.advertisement.UpdateAdvertisementDto;
import org.example.controller.util.ModelDtoConverter;

import org.example.core.advertising.AdvertisementService;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.image.StorageService;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final StorageService storageService;

    @GetMapping(Mappings.ADVERTISEMENTS)
    @CrossOrigin
    public Slice<AdvertisementDto> getAdvertisements(@RequestParam(name = AttributeNames.PAGE_NUMBER, required = false,defaultValue = "0") Integer page,
                                                     @RequestParam(name = AttributeNames.PAGE_SIZE, required = false,defaultValue = AttributeNames.ADVERTISEMENTS_PAGE_SIZE) Integer size,
                                                     @RequestParam(name = AttributeNames.SORT_ORDER, required = false,defaultValue = AttributeNames.DESC) String sortOrder,
                                                     @RequestParam(name = AttributeNames.SORT_PARAM, required = false,defaultValue = AttributeNames.ADVERTISEMENTS_DEFAULT_SORT_PARAM) String sortParam,
                                                     @RequestParam(required = false) Map<String,String> searchParams){

        AdvertisementQueryParams
            adQueryParams = ModelDtoConverter.convertSearchParamsToObject(searchParams,AdvertisementQueryParams.class);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder),
                advertisementService.convertSortParamToValidForm(sortParam)));
        return advertisementService.getAdvertisements(adQueryParams,pageable)
                .map(ModelDtoConverter::convertAdvertisementModelToDto);
    }
    @GetMapping(Mappings.ADVERTISEMENT+"/{id}")
    @CrossOrigin
    public AdvertisementAllDataDto getAdvertisementsById(@PathVariable("id") int id)
            throws UnknownAdvertisementException, UnknownUserException {
        return ModelDtoConverter.convertAdvertisementModelToAllDto(advertisementService.getAdvertisementById(id));
    }
    @GetMapping(Mappings.ADVERTISEMENTS+"/{username}")
    @CrossOrigin
    public Page<AdvertisementDto> getAdvertisementsByUsername(@PathVariable("username") String username,
                                    @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false,defaultValue = "0") Integer page,
                                    @RequestParam(name = AttributeNames.PAGE_SIZE, required = false,defaultValue = AttributeNames.USER_ADVERTISEMENTS_PAGE_SIZE) Integer size,
                                    @RequestParam(name =AttributeNames.SORT_ORDER, required = false,defaultValue = AttributeNames.DESC) String sortOrder,
                                    @RequestParam(name ="state", required = false,defaultValue = "ACTIVE") AdState state,
                                    @RequestParam(name = AttributeNames.SORT_PARAM, required = false,defaultValue = AttributeNames.ADVERTISEMENTS_DEFAULT_SORT_PARAM) String sortParam)
                                    throws UnknownUserException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)){
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortParam));
        return advertisementService.getAdvertisementsByUsername(username,pageable,state)
                .map(ModelDtoConverter::convertAdvertisementModelToDto);
    }
    @GetMapping(Mappings.ADVERTISEMENTS+"/{username}/"+Mappings.SAVED)
    @CrossOrigin
    public Collection<SavedAdDto> getSavedAdvertisementsByUsername(@PathVariable("username") String username)
            throws UnknownUserException {
        return advertisementService.getSavedAdvertisementsByUsername(username).stream()
                .map(advertisement -> SavedAdDto.builder()
                        .adId(advertisement.getId()).title(advertisement.getTitle()).build()).collect(Collectors.toList());
    }
    @GetMapping(Mappings.ADVERTISEMENT+"/{id}/"+Mappings.DETAILS)
    @CrossOrigin
    public AdvertisementDetailsDto getAdvertisementDetails(@PathVariable("id") int id) throws UnknownAdvertisementException {
        return ModelDtoConverter.convertAdDetailsToDto(advertisementService.getAdDetailsById(id));
    }
    @PutMapping(Mappings.ADVERTISEMENT+"/{id}/"+Mappings.DETAILS)
    public void updateAdvertisementDetails(@PathVariable("id") int id, @Valid @RequestBody AdvertisementDetailsDto advertisementDetailsDto,
                                           BindingResult bindingResult) throws UnknownAdvertisementException, ValidationException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for sending user message",errors);
        }
        AdDetails adDetails = ModelDtoConverter.convertAdvertisementDetailsDtoToModel(advertisementDetailsDto);
        adDetails.setAdId(id);
        advertisementService.updateAdDetails(adDetails);
    }
    @PutMapping(Mappings.ADVERTISEMENT+"/{id}")
    @CrossOrigin
    public void updateAdvertisement(@Valid @ModelAttribute UpdateAdvertisementDto updateAdvertisementDto, @PathVariable int id,
                                    @RequestParam MultipartFile[] images ,BindingResult bindingResult) throws UnknownAdvertisementException, ValidationException,
        UnknownCategoryException, UnknownUserException, FileUploadException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for updated advertisement",errors);
        }
        Advertisement advertisement = ModelDtoConverter.createNewAdvertisementFromDto(updateAdvertisementDto);
        AdDetails adDetails = ModelDtoConverter.convertAdvertisementDetailsDtoToModel(updateAdvertisementDto);
        adDetails.setAdId(updateAdvertisementDto.getId());
        advertisement.setId(id);
        advertisementService.updateAllAdvertisement(advertisement,adDetails,images);
    }
    @PostMapping(value= Mappings.ADVERTISEMENT,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    public void createAdvertisement(@Valid @ModelAttribute CreateAdvertisementDto createAdvertisementDto,
                                    @RequestParam MultipartFile[] images,BindingResult bindingResult) throws ValidationException, UnknownCategoryException,
            UnknownUserException, UnknownAdvertisementException, FileUploadException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(createAdvertisementDto.getCreator())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for creating advertisement", errors);
        }
        Advertisement advertisement = ModelDtoConverter.createNewAdvertisementFromDto(createAdvertisementDto);
        AdDetails adDetails = ModelDtoConverter.convertAdvertisementDetailsDtoToModel(createAdvertisementDto);
        advertisementService.createAdvertisement(advertisement,adDetails,images);
    }
    @PatchMapping(Mappings.ADVERTISEMENT+"/{id}/{state}")
    @CrossOrigin
    public void changeState(@PathVariable("id") int id,@PathVariable AdState state) throws UnknownAdvertisementException {
      advertisementService.changeState(id,state,SecurityContextHolder.getContext().getAuthentication().getName());
    }
    @DeleteMapping(Mappings.ADVERTISEMENT+"/{id}")
    public void deleteAdvertisement(@PathVariable("id") int id) throws UnknownAdvertisementException {
        advertisementService.deleteAdvertisement(id);
    }
    @DeleteMapping(Mappings.ADVERTISEMENT+"/{id}/"+Mappings.IMG)
    public void deleteAdvertisementImages(@PathVariable("id") int id,@RequestBody List<String> imagePaths) throws UnknownAdvertisementException {
        advertisementService.removeImages(id,imagePaths);
    }
    @GetMapping(Mappings.ADVERTISEMENT+"/"+Mappings.BRANDS+"/{category}")
    @CrossOrigin
    public List<String> getBrandNamesByCategory(@PathVariable("category") String category) {
      return advertisementService.getBrandNamesByCategory(category);
    }
    @GetMapping(Mappings.ADVERTISEMENT+"/"+Mappings.BRAND+"/{brandName}/"+Mappings.TYPES)
    @CrossOrigin
    public List<String> getCarTypesByBrand(@PathVariable("brandName") String brandName,@RequestParam(required = false) String category) {
        return advertisementService.getCarTypesByBrandName(category,brandName);
    }

    @GetMapping(Mappings.ADVERTISEMENT+"/"+Mappings.CATEGORIES)
    @CrossOrigin
    public List<String> getCategories() {
        return advertisementService.getCategories();
    }

    @GetMapping(value = Mappings.IMG+"/{path}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public @ResponseBody byte[] getAdImage(@PathVariable String path, @PathVariable String filename){
        //todo megprobalni atmeretezni a kepete, ellenorizve hogy valoban kep e
        //todo adatbaziban teljes path nem tarolni
        try{
            InputStream in = storageService.loadAsResource(filename,path).getInputStream();
            byte[] data = in.readAllBytes();
            in.close();
            return data;
        }catch (ResourceAccessException | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Image not found",e);
        }
    }
}
