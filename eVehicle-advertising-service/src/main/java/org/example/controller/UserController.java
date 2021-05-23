package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.Mappings;
import org.example.controller.dto.user.AuthenticationRequestDto;
import org.example.controller.dto.user.AuthenticationResponseDto;
import org.example.controller.dto.user.UpdateUserRolesDto;
import org.example.controller.dto.user.UserBasicDto;
import org.example.controller.dto.user.UserDataDto;
import org.example.controller.dto.user.UserMarkedAdDto;
import org.example.controller.dto.user.UserRegistrationDto;
import org.example.controller.dto.user.UserRolesDto;
import org.example.controller.dto.user.UsernameUpdateDto;
import org.example.controller.util.ModelDtoConverter;

import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.security.AuthException;
import org.example.core.user.UserService;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


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
            throws UnknownUserException, org.example.exceptions.ValidationException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userDataDto.getUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new org.example.exceptions.ValidationException("Validation failed for UserDataDto",errors);
        }
        userService.updateUserData(ModelDtoConverter.convertUserDataDtoToUserData(userDataDto));
    }
    @PostMapping(Mappings.USER)
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto, BindingResult bindingResult)
            throws UnknownRoleException, EmailAlreadyExistsException, UsernameAlreadyExistsException,
        org.example.exceptions.ValidationException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new org.example.exceptions.ValidationException("Validation failed UserRegistrationDto",errors);
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
            throws org.example.exceptions.ValidationException, UnknownRoleException, UnknownUserException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new org.example.exceptions.ValidationException("Validation failed userRolesDto",errors);
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
            throws UnknownUserException, UnknownAdvertisementException, org.example.exceptions.ValidationException, MaximumSavedAdsReachedException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userMarkedAdDto.getUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new org.example.exceptions.ValidationException("Validation failed userMarkedAdDto",errors);
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
            throws org.example.exceptions.ValidationException, UnknownUserException, UsernameAlreadyExistsException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(oldUsername)){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new org.example.exceptions.ValidationException("Validation failed usernameUpdateDto",errors);
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
