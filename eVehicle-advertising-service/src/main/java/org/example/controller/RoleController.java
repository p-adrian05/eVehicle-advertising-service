package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.Mappings;
import org.example.controller.dto.user.UpdateUserRolesDto;
import org.example.controller.util.ModelDtoConverter;
import org.example.core.role.RoleService;
import org.example.core.role.exception.RoleModificationException;
import org.example.core.role.model.Role;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController {

    private final RoleService roleService;

    @GetMapping(Mappings.USER+"/{username}/"+Mappings.ROLES)
    public List<Role> getRolesByUsername(@PathVariable String username) {
        return roleService.getRolesByUsername(username);
    }
    @GetMapping(Mappings.ROLES)
    public List<Role> getRoles() {
        return roleService.readRoles();
    }
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

}
