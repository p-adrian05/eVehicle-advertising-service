package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.rate.CreateUserRateAsSellerDto;
import org.example.controller.dto.rate.CreateUserRateDto;
import org.example.controller.dto.rate.UpdateRateDto;
import org.example.controller.dto.rate.UserRateBasicDto;
import org.example.controller.dto.rate.UserRateDto;
import org.example.controller.util.ModelDtoConverter;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.rating.UserRateService;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateState;
import org.example.core.rating.persistence.repository.RateQueryParams;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
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
        return userRateService.getRates(rateQueryParams,pageable)
                .map(ModelDtoConverter::convertUserRateModelToDto);
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
                .map(ModelDtoConverter::convertUserRateModelToBasicDto);
    }

    @PostMapping(Mappings.RATE+"/buyer")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createRateAsBuyer(@Valid @RequestBody CreateUserRateDto createUserRateDto, BindingResult bindingResult)
            throws UnknownAdvertisementException, UnknownUserException, UserRateAlreadyExistsException, ValidationException,
        UnknownUserRateException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(createUserRateDto.getRatingUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for creating rate as buyer",errors);
        }
        UserRate userRate = ModelDtoConverter.convertCreateUserRateDtoToModel(createUserRateDto);
        userRate.setRatedState(UserRateState.SELLER);
        userRateService.createUserRate(userRate);
    }
    @PostMapping(Mappings.RATE+"/seller")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createRateAsSeller(@Valid @RequestBody CreateUserRateAsSellerDto createUserRateAsSellerDto, BindingResult bindingResult)
            throws UnknownAdvertisementException, UnknownUserException, UserRateAlreadyExistsException, ValidationException, UnknownUserRateException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(createUserRateAsSellerDto.getRatingUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for creating rate as seller",errors);
        }
        UserRate userRate = ModelDtoConverter.convertCreateUserRateDtoToModel(createUserRateAsSellerDto);
        userRate.setRatedState(UserRateState.BUYER);
        userRate.setActivationCode(createUserRateAsSellerDto.getActivationCode());
        userRateService.createUserRate(userRate);
    }
    @PatchMapping(Mappings.RATE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRate(@Valid @RequestBody UpdateRateDto updateRateDto, BindingResult bindingResult)
            throws ValidationException, UnknownAdvertisementException, UnknownUserException, UnknownUserRateException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for updating user rate",errors);
        }
        userRateService.updateUserRate(UserRate.builder()
        .id(updateRateDto.getRateId())
        .rateState(updateRateDto.getRateState())
        .description(updateRateDto.getDescription()).build());
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