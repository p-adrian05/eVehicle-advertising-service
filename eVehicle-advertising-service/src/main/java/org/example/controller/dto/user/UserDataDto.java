package org.example.controller.dto.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDataDto {

    @NotNull(message = "username: cannot be null")
    @NotEmpty(message = "username: cannot be empty")
    private String username;
    @Size(min = 2,max = 20,message = "username: min 2 characters required, max 20 characters allowed")
    @Pattern(regexp = "^[A-Z].*$",message = "city: invalid")
    private String city;
    @Size(max = 20,message = "fullName: max length 50")
    private String fullName;

    @Email(message = "email: invalid")
    private String publicEmail;
    @Pattern(regexp = "^(([+][36]{2})?)(([0][6])?[0-9]{9})$",message = "phoneNumber: invalid")
    private String phoneNumber;
}
