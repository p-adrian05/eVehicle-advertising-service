package org.example.controller.dto.user;

import lombok.Builder;
import lombok.Data;
import org.example.core.role.model.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UpdateUserRolesDto {

    private String operation;
    @NotNull(message = "username: cannot be null")
    @NotEmpty(message = "username: cannot be empty")
    private String username;
    @NotNull(message = "roles: cannot be null")
    @NotEmpty(message = "roles: cannot be empty")
    private Role role;

}
