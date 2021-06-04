package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.advertisement.AdvertisementDetailsDto;
import org.example.controller.dto.advertisement.CreateAdvertisementDto;
import org.example.controller.dto.advertisement.SavedAdDto;
import org.example.controller.dto.user.UserMarkedAdDto;
import org.example.controller.util.ModelDtoConverter;
import org.example.core.advertising.AdUtilService;
import org.example.core.advertising.AdvertisementService;
import org.example.core.advertising.exception.MaximumSavedAdsReachedException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdLabelDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.security.AuthException;
import org.example.core.storage.AdImageStorageService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdUtilService adUtilService;
    private final AdImageStorageService storageService;

    @GetMapping(Mappings.ADVERTISEMENTS)
    @CrossOrigin
    public Slice<AdLabelDto> getAdvertisements(
        @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false, defaultValue = "0") Integer page,
        @RequestParam(name = AttributeNames.PAGE_SIZE, required = false, defaultValue = AttributeNames.ADVERTISEMENTS_PAGE_SIZE)
            Integer size,
        @RequestParam(name = AttributeNames.SORT_ORDER, required = false, defaultValue = AttributeNames.DESC)
            String sortOrder,
        @RequestParam(name = AttributeNames.SORT_PARAM, required = false, defaultValue = AttributeNames.ADVERTISEMENTS_DEFAULT_SORT_PARAM)
            String sortParam,
        @RequestParam(name = AttributeNames.CURRENCY, required = false,defaultValue = "HUF") String currency,
        @RequestParam(required = false) Map<String, String> searchParams) {

        AdvertisementQueryParams
            adQueryParams = ModelDtoConverter.convertSearchParamsToObject(searchParams, AdvertisementQueryParams.class);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder),
            adUtilService.convertSortParamToValidForm(sortParam)));
        return advertisementService.getAdvertisements(adQueryParams, pageable, Currency.getInstance(currency));
    }

    @GetMapping(Mappings.ADVERTISEMENT + "/{id}")
    @CrossOrigin
    public AdvertisementDto getAdvertisementById(@PathVariable("id") int id,@RequestParam(name = AttributeNames.CURRENCY,  required = false,defaultValue = "HUF") String currency) {
        Optional<AdvertisementDto> advertisementDto = advertisementService.getAdvertisementById(id, Currency.getInstance(currency));
        if (advertisementDto.isPresent()) {
            return advertisementDto.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No advertisement found");
    }

    @GetMapping(Mappings.ADVERTISEMENTS + "/{username}")
    @CrossOrigin
    public Page<AdLabelDto> getAdvertisementsByUsername(@PathVariable("username") String username,
                                                        @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false, defaultValue = "0")
                                                            Integer page,
                                                        @RequestParam(name = AttributeNames.PAGE_SIZE, required = false, defaultValue = AttributeNames.USER_ADVERTISEMENTS_PAGE_SIZE)
                                                            Integer size,
                                                        @RequestParam(name = AttributeNames.CURRENCY, required = false, defaultValue = "HUF") String currency,
                                                        @RequestParam(name = AttributeNames.SORT_ORDER, required = false, defaultValue = AttributeNames.DESC)
                                                            String sortOrder,
                                                        @RequestParam(name = "state", required = false, defaultValue = "ACTIVE")
                                                            AdState state,
                                                        @RequestParam(name = AttributeNames.SORT_PARAM, required = false, defaultValue = AttributeNames.ADVERTISEMENTS_DEFAULT_SORT_PARAM)
                                                            String sortParam) {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortParam));
        return advertisementService.getAdvertisementsByUsername(username, pageable, state,Currency.getInstance(currency));
    }

    @GetMapping(Mappings.ADVERTISEMENTS + "/{username}/" + Mappings.SAVED)
    @CrossOrigin
    public Collection<SavedAdDto> getSavedAdvertisementsByUsername(@PathVariable("username") String username) {
        return adUtilService.getSavedAdvertisementTitlesByUsername(username).entrySet().stream()
            .map(entry -> SavedAdDto.builder()
                .adId(entry.getKey()).title(entry.getValue()).build()).collect(Collectors.toList());
    }

    @GetMapping(Mappings.ADVERTISEMENT + "/{id}/" + Mappings.DETAILS)
    @CrossOrigin
    public AdDetailsDto getAdvertisementDetails(@PathVariable("id") int id) {
        Optional<AdDetailsDto> adDetailsDto = advertisementService.getAdDetailsById(id);
        if (adDetailsDto.isPresent()) {
            return adDetailsDto.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No advertisement found");
    }

    @PutMapping(Mappings.ADVERTISEMENT + "/{id}")
    @CrossOrigin
    public void updateAdvertisement(@Valid @ModelAttribute CreateAdvertisementDto createAdvertisementDto,
                                    @Valid @ModelAttribute AdvertisementDetailsDto advertisementDetailsDto,
                                    @PathVariable int id,
                                    @RequestParam MultipartFile[] images, BindingResult bindingResult)
        throws UnknownAdvertisementException, ValidationException,
        UnknownCategoryException, UnknownUserException, FileUploadException {
        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for updated advertisement", errors);
        }
        if(!checkIfImageIsValid(images)){
            throw new ValidationException("Validation failed for images", List.of("Wrong image format"));
        }
        UpdateAdvertisementDto advertisement = UpdateAdvertisementDto.builder()
            .id(id)
            .category(createAdvertisementDto.getCategory())
            .brand(createAdvertisementDto.getBrand())
            .condition(createAdvertisementDto.getCondition())
            .price(createAdvertisementDto.getPrice())
            .title(createAdvertisementDto.getTitle())
            .currency(createAdvertisementDto.getCurrency())
            .type(createAdvertisementDto.getType())
            .build();
        AdDetailsDto adDetails = ModelDtoConverter.convertAdvertisementDetailsDtoToModel(advertisementDetailsDto, id);
        advertisementService.updateAdvertisementWithDetails(advertisement, adDetails, images);
    }

    @PostMapping(value = Mappings.ADVERTISEMENT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    public void createAdvertisement(@Valid @ModelAttribute CreateAdvertisementDto createAdvertisementDto,
                                    @Valid @ModelAttribute AdvertisementDetailsDto advertisementDetailsDto,
                                    @RequestParam MultipartFile[] images, BindingResult bindingResult)
        throws ValidationException, UnknownCategoryException,
        UnknownUserException, UnknownAdvertisementException, FileUploadException, IOException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName()
            .equals(createAdvertisementDto.getCreator())) {
            throw new AuthException("Access Denied");
        }
        if(!checkIfImageIsValid(images)){
            throw new ValidationException("Validation failed for images", List.of("Wrong image format"));
        }
        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for creating advertisement", errors);
        }
        CreateAdDto advertisement = ModelDtoConverter.createNewAdvertisementFromDto(createAdvertisementDto);
        AdDetailsDto adDetailsDto = ModelDtoConverter.convertAdvertisementDetailsDtoToModel(advertisementDetailsDto, 0);

        advertisementService.createAdvertisement(advertisement, adDetailsDto, images);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage(images[0].getBytes(), 300, 300);
        image.getScaledInstance(300, 200, 0);
    }

    private boolean checkIfImageIsValid(MultipartFile[] multipartFiles) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (MultipartFile multipartFile : multipartFiles) {
            try {
                toolkit.createImage(multipartFile.getBytes(), 300, 300).getScaledInstance(100, 100, 1);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @PatchMapping(Mappings.ADVERTISEMENT + "/{id}/{state}")
    @CrossOrigin
    public void changeState(@PathVariable("id") int id, @PathVariable AdState state)
        throws UnknownAdvertisementException {
        advertisementService.changeState(id, state, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping(Mappings.ADVERTISEMENT + "/" + Mappings.BRANDS + "/{category}")
    @CrossOrigin
    public List<String> getBrandNamesByCategory(@PathVariable("category") String category) {
        return adUtilService.getBrandNamesByCategory(category);
    }

    @GetMapping(Mappings.ADVERTISEMENT + "/" + Mappings.BRAND + "/{brandName}/" + Mappings.TYPES)
    @CrossOrigin
    public List<String> getCarTypesByBrand(@PathVariable("brandName") String brandName,
                                           @RequestParam(required = false) String category) {
        return adUtilService.getCarTypesByBrandName(category, brandName);
    }

    @GetMapping(Mappings.ADVERTISEMENT + "/" + Mappings.CATEGORIES)
    @CrossOrigin
    public List<String> getCategories() {
        return adUtilService.getCategories();
    }

    @GetMapping(value = Mappings.IMG + "/{path}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public @ResponseBody
    byte[] getAdImage(@PathVariable String path, @PathVariable String filename) {
        try {
            InputStream in = storageService.loadAdImage(path + "/" + filename).getInputStream();
            byte[] data = in.readAllBytes();
            in.close();
            return data;
        } catch (ResourceAccessException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found", e);
        }
    }

    @PatchMapping(Mappings.USER_SAVED_AD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyMarkedAd(@Valid @RequestBody UserMarkedAdDto userMarkedAdDto, BindingResult bindingResult)
        throws UnknownUserException, UnknownAdvertisementException, ValidationException,
        MaximumSavedAdsReachedException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(userMarkedAdDto.getUsername())) {
            throw new AuthException("Access Denied");
        }
        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed userMarkedAdDto", errors);
        }
        if (userMarkedAdDto.getOperation().equals("add")) {
            adUtilService.addSaveAd(userMarkedAdDto.getUsername(), userMarkedAdDto.getAdId());
        } else if (userMarkedAdDto.getOperation().equals("delete")) {
            adUtilService.removeSaveAd(userMarkedAdDto.getUsername(), userMarkedAdDto.getAdId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("unknown operation: %s", userMarkedAdDto.getOperation()));
        }
    }
}
