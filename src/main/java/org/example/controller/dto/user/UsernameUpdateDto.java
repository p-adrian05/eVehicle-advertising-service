package org.example.controller.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UsernameUpdateDto {

    @NotNull(message = "newUsername: cannot be null")
    @NotEmpty(message = "newUsername: cannot be empty")
    private String newUsername;
}
