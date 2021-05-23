package org.example.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesDto {

    @NotNull(message = "username: cannot be null")
    @NotEmpty(message = "username: cannot be empty")
    private String username;
    @NotNull(message = "roles: cannot be null")
    @NotEmpty(message = "roles: cannot be empty")
    private List<String> roles;

}
