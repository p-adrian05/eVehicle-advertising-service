package org.example.controller.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class UserMarkedAdDto {
    @NotNull(message = "advertisement id: cannot be null")
    private int adId;
    @NotNull(message = "username: cannot be null")
    @NotEmpty(message = "username: cannot be empty")
    private String username;

    private String operation;
}
