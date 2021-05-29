package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.Mappings;
import org.example.controller.dto.user.AuthenticationRequestDto;
import org.example.controller.dto.user.AuthenticationResponseDto;
import org.example.controller.dto.user.UpdateUserDataDto;
import org.example.controller.dto.user.UpdateUserRolesDto;
import org.example.controller.dto.user.UserBasicDto;
import org.example.controller.dto.user.UserRegistrationDto;
import org.example.controller.dto.user.UsernameUpdateDto;
import org.example.controller.util.ModelDtoConverter;
import org.example.core.role.RoleService;
import org.example.core.role.exception.RoleModificationException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.security.AuthException;
import org.example.core.user.LoginService;
import org.example.core.user.UserDataService;
import org.example.core.user.UserService;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.core.user.model.CreateUserDto;
import org.example.core.user.model.UserDataDto;
import org.example.core.user.model.UserDto;
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
import java.util.Optional;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final LoginService loginService;
    private final UserDataService userDataService;


    @GetMapping(Mappings.USER+"/{username}")
    @CrossOrigin
    public UserBasicDto getUserBasicDataByUsername(@PathVariable String username) {
        Optional<UserDto> userDto = userService.getUserByName(username);
        if(userDto.isPresent()){
            return ModelDtoConverter.convertUserToUserBasicDto(userDto.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
    }
    @GetMapping(Mappings.USER+"/{username}/"+Mappings.DETAILS)
    @CrossOrigin
    public UserDataDto getUserData(@PathVariable String username) throws UnknownUserException {
        Optional<UserDataDto> userDataDto = userDataService.getUserData(username);
        if(userDataDto.isPresent()){
            return userDataDto.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
    }
    @PutMapping(Mappings.USER_DETAILS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserData(@Valid @RequestBody UpdateUserDataDto updateUserDataDto, BindingResult bindingResult)
            throws UnknownUserException, ValidationException {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(updateUserDataDto.getUsername())){
            throw new AuthException("Access Denied");
        }
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for UserDataDto",errors);
        }
        userDataService.updateUserData(ModelDtoConverter.convertUserDataDtoToUserData(updateUserDataDto));
    }
    @PostMapping(Mappings.USER)
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto, BindingResult bindingResult)
            throws UnknownRoleException, EmailAlreadyExistsException, UsernameAlreadyExistsException,
       ValidationException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed UserRegistrationDto",errors);
        }
        userService.createUser(CreateUserDto.builder()
            .username(userRegistrationDto.getUsername())
            .email(userRegistrationDto.getEmail())
            .password(userRegistrationDto.getPassword())
            .build());
    }
//    @GetMapping(Mappings.USER+"/{username}/"+Mappings.ROLES)
//    public String getRoles(@PathVariable String username) throws UnknownUserException {
//        return roleService.readRoles(username);
//    }
    @PatchMapping(Mappings.USER_ROLES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyRole(@Valid @RequestBody UpdateUserRolesDto updateUserRolesDto,BindingResult bindingResult)
        throws ValidationException, UnknownUserException, RoleModificationException {
        if(bindingResult.hasErrors()){
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed userRolesDto",errors);
        }
        if(updateUserRolesDto.getOperation().equals("add")){
            roleService.addRole(updateUserRolesDto.getUsername(),updateUserRolesDto.getRole());
        }else if(updateUserRolesDto.getOperation().equals("delete")){
            roleService.removeRole(updateUserRolesDto.getUsername(),updateUserRolesDto.getRole());
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("unknown operation: %s",updateUserRolesDto.getOperation()));
        }
    }
//    @PatchMapping(Mappings.USER_SAVED_AD)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void modifyMarkedAd(@Valid @RequestBody UserMarkedAdDto userMarkedAdDto, BindingResult bindingResult)
//            throws UnknownUserException, UnknownAdvertisementException, ValidationException {
//        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userMarkedAdDto.getUsername())){
//            throw new AuthException("Access Denied");
//        }
//        if(bindingResult.hasErrors()){
//            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
//            throw new ValidationException("Validation failed userMarkedAdDto",errors);
//        }
//        if(userMarkedAdDto.getOperation().equals("add")){
//            userService.addSaveAd(userMarkedAdDto.getUsername(),userMarkedAdDto.getAdId());
//        }else if(userMarkedAdDto.getOperation().equals("delete")){
//            userService.removeSaveAd(userMarkedAdDto.getUsername(),userMarkedAdDto.getAdId());
//        }else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    String.format("unknown operation: %s",userMarkedAdDto.getOperation()));
//        }
//    }
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
        throws AuthException, javax.security.auth.message.AuthException {

        return new AuthenticationResponseDto(
                loginService.login(authenticationRequestDto.getUsername(),authenticationRequestDto.getPassword()));
    }

    @GetMapping("/authenticate/activate/{code}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String registrationActivation(@PathVariable String code)
        throws AuthException, javax.security.auth.message.AuthException {
        loginService.activateUser(code);
        return "Successful activation";
    }

}
