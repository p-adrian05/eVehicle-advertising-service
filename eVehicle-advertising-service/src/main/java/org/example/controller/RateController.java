package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.rate.CreateUserRateAsSellerDto;
import org.example.controller.dto.rate.CreateUserRateDto;
import org.example.controller.dto.rate.RateAdvertisementDto;
import org.example.controller.dto.rate.UserRateBasicDto;

import org.example.controller.util.ModelDtoConverter;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.rating.UserRateService;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.rating.model.UserRateDto;
import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateState;
import org.example.core.rating.persistence.repository.RateQueryParams;
import org.example.security.exception.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateController {

    private final UserRateService userRateService;


    @GetMapping(Mappings.RATES)
    @CrossOrigin
    public Page<UserRateDto> getClosedRates(
                                      @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false,defaultValue = "0") Integer page,
                                      @RequestParam(name = AttributeNames.PAGE_SIZE, required = false,defaultValue = AttributeNames.RATES_PAGE_SIZE) Integer size,
                                      @RequestParam() Map<String,String> searchParams){

        RateQueryParams rateQueryParams = ModelDtoConverter.convertSearchParamsToObject(searchParams,RateQueryParams.class);
        Pageable pageable = PageRequest.of(page, size);
        rateQueryParams.setRateStatus(RateStatus.CLOSED);
        return userRateService.getRates(rateQueryParams,pageable);
    }
    @GetMapping(Mappings.RATES+"/{username}")
    @CrossOrigin
    public Page<UserRateBasicDto> getRatesWithBasicData(@RequestParam() Map<String,String> searchParams,@PathVariable("username") String username,
                                                          @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false,defaultValue = "0") Integer page,
                                                          @RequestParam(name = AttributeNames.PAGE_SIZE, required = false,defaultValue = AttributeNames.RATES_PAGE_SIZE) Integer size){
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)){
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest.of(page, size);
        RateQueryParams rateQueryParams = ModelDtoConverter.convertSearchParamsToObject(searchParams,RateQueryParams.class);
        rateQueryParams.setRatedUsername(username);
        return userRateService.getRates(rateQueryParams,pageable)
                .map(userRate ->  UserRateBasicDto.builder()
            .advertisement(RateAdvertisementDto.builder()
                .id(userRate.getAdvertisement().getId())
                .price(userRate.getAdvertisement().getPrice())
                .title(userRate.getAdvertisement().getTitle())
                .build())
            .created(userRate.getCreated())
            .ratedState(userRate.getRatedState())
            .ratedUsername(userRate.getRatedUsername())
            .ratingUsername(userRate.getRatingUsername())
            .ratingUserProfileImageId(userRate.getRatingUserProfileImageId())
                    .status(userRate.getStatus())
                    .activationCode(userRate.getActivationCode())
            .build());
    }

    @PostMapping(Mappings.RATE+"/buyer")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createRateAsBuyer(@Valid @RequestBody CreateUserRateDto createUserRateDto)
        throws UnknownAdvertisementException, UnknownUserException, UserRateAlreadyExistsException,
        UnknownUserRateException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(createUserRateDto.getRatingUsername())){
            throw new AuthException("Access Denied");
        }
        UserRateDto userRate = UserRateDto.builder()
            .rateState(createUserRateDto.getRateState())
            .advertisement(AdvertisementDto.builder().id(createUserRateDto.getAdId()).build())
            .description(createUserRateDto.getDescription())
            .ratedUsername(createUserRateDto.getRatedUsername())
            .ratingUsername(createUserRateDto.getRatingUsername())
            .ratedState(UserRateState.SELLER)
            .build();
        userRateService.createRate(userRate);
    }
    @PostMapping(Mappings.RATE+"/seller")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createRateAsSeller(@Valid @RequestBody CreateUserRateAsSellerDto createUserRateAsSellerDto)
        throws UnknownAdvertisementException, UnknownUserException,
        UnknownUserRateException, UserRateAlreadyExistsException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(createUserRateAsSellerDto.getRatingUsername())){
            throw new AuthException("Access Denied");
        }
        UserRateDto userRate = UserRateDto.builder()
            .rateState(createUserRateAsSellerDto.getRateState())
            .advertisement(AdvertisementDto.builder().id(createUserRateAsSellerDto.getAdId()).build())
            .description(createUserRateAsSellerDto.getDescription())
            .ratedUsername(createUserRateAsSellerDto.getRatedUsername())
            .ratingUsername(createUserRateAsSellerDto.getRatingUsername())
            .ratedState(UserRateState.BUYER)
            .activationCode(createUserRateAsSellerDto.getActivationCode())
            .build();
        userRateService.createRate(userRate);
    }

    @GetMapping(Mappings.RATE_COUNT+"/{username}")
    @CrossOrigin
    public Map<RateState,Integer> getRatesCount(@PathVariable("username") String username) throws UnknownUserException {
        return userRateService.getRatesCountByUsernameAndRateState(username);
    }
    @DeleteMapping(Mappings.RATE +"/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRate(@PathVariable("id") int id) throws UnknownUserRateException {
         userRateService.deleteUserRate(id);
    }

}
