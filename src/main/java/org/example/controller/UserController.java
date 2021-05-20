package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.Mappings;
import org.example.controller.dto.user.*;
import org.example.controller.util.ModelDtoConverter;
import org.example.exceptions.*;


import org.example.services.UserService;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.validation.Valid;
import java.util.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @GetMapping(Mappings.USER+"/{username}")
    @CrossOrigin
    public UserBasicDto getUserBasicDataByUsername(@PathVariable String username) throws UnknownUserException {
        return ModelDtoConverter.convertUserToUserBasicDto(userService.getUserByName(username));
    }
    @GetMapping(Mappings.USER+"/{username}/"+Mappings.DETAILS)
    @CrossOrigin
    public UserDataDto getUserData(@PathVariable String username) throws UnknownUserException {
        return ModelDtoConverter.convertUserDataToUserDataDto(userService.getUserData(username));
    }
    @PutMapping(Mappings.USER_DETAILS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserData(@Valid @RequestBody UserDataDto userDataDto, BindingResult bindingResult)
            throws UnknownUserException, ValidationException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userDataDto.getUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for UserDataDto",errors);
        }
        userService.updateUserData(ModelDtoConverter.convertUserDataDtoToUserData(userDataDto));
    }
    @PostMapping(Mappings.USER)
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto, BindingResult bindingResult)
            throws UnknownRoleException, EmailAlreadyExistsException, UsernameAlreadyExistsException, ValidationException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed UserRegistrationDto",errors);
        }
        userService.createUser(ModelDtoConverter.convertUserRegistrationDtoToUser(userRegistrationDto));
    }
    @GetMapping(Mappings.USER+"/{username}/"+Mappings.ROLES)
    public UserRolesDto getRoles(@PathVariable String username) throws UnknownUserException {
        return UserRolesDto.builder().roles(userService.getRoles(username)).username(username).build();
    }
    @PatchMapping(Mappings.USER_ROLES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyRoles(@Valid @RequestBody UpdateUserRolesDto updateUserRolesDto,BindingResult bindingResult)
            throws ValidationException, UnknownRoleException, UnknownUserException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed userRolesDto",errors);
        }
        if(updateUserRolesDto.getOperation().equals("add")){
            userService.addRole(updateUserRolesDto.getUsername(),updateUserRolesDto.getRoles());
        }else if(updateUserRolesDto.getOperation().equals("delete")){
            userService.removeRole(updateUserRolesDto.getUsername(),updateUserRolesDto.getRoles());
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("unknown operation: %s",updateUserRolesDto.getOperation()));
        }
    }
    @PatchMapping(Mappings.USER_SAVED_AD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyMarkedAd(@Valid @RequestBody UserMarkedAdDto userMarkedAdDto, BindingResult bindingResult)
            throws UnknownUserException, UnknownAdvertisementException, ValidationException, MaximumSavedAdsReachedException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userMarkedAdDto.getUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed userMarkedAdDto",errors);
        }
        if(userMarkedAdDto.getOperation().equals("add")){
            userService.addSaveAd(userMarkedAdDto.getUsername(),userMarkedAdDto.getAdId());
        }else if(userMarkedAdDto.getOperation().equals("delete")){
            userService.removeSaveAd(userMarkedAdDto.getUsername(),userMarkedAdDto.getAdId());
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("unknown operation: %s",userMarkedAdDto.getOperation()));
        }
    }
    @PatchMapping(Mappings.USER+"/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUsername(@PathVariable("username") String oldUsername,
                               @Valid @RequestBody UsernameUpdateDto usernameUpdateDto, BindingResult bindingResult)
            throws ValidationException, UnknownUserException, UsernameAlreadyExistsException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(oldUsername)){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed usernameUpdateDto",errors);
        }
        userService.updateUsername(oldUsername,usernameUpdateDto.getNewUsername());
    }
    @DeleteMapping(Mappings.USER+"/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) throws UnknownUserException {
        userService.deleteUser(username);
    }

    @PostMapping("/authenticate")
    @CrossOrigin
    public AuthenticationResponseDto authenticate(@RequestBody AuthenticationRequestDto authenticationRequestDto)
            throws AuthException {

        return new AuthenticationResponseDto(
                userService.login(authenticationRequestDto.getUsername(),authenticationRequestDto.getPassword()));
    }

    @GetMapping("/authenticate/activate/{code}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String registrationActivation(@PathVariable String code)
            throws AuthException {
        userService.activateUser(code);
        return "Successful activation";
    }

}
