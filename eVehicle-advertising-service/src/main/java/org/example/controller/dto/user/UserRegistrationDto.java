package org.example.controller.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.controller.validation.FieldsVerification;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@FieldsVerification.List( value = {
        @FieldsVerification(
                field = "password",
                fieldMatch = "verifyPassword",
                message = "password: passwords do not match"
        )
})
public class UserRegistrationDto {

    @Size(min = 2,max = 15,message = "username: min 2 characters required, max 15 characters allowed")
    @NotEmpty(message = "username: cannot be empty")
    private String username;

    @Email(message = "email: invalid")
    @NotEmpty(message = "email: cannot be empty")
    private String email;

    @NotEmpty(message = "password: cannot be empty")
    @Size(min = 8,message = "password: min 8 characters required")
    @Pattern(regexp = "(?=.*[0-9]).{8,}",message = "password: a digit must occur at least once")
    @Pattern(regexp = "(?=.*[a-z]).{8,}",message = "password: a lower case letter must occur at least once")
    @Pattern(regexp = "(?=.*[A-Z]).{8,}",message = "password: an upper case letter must occur at least once")
    @Pattern(regexp = "(?=.*[@#$%^&+=]).{8,}",message = "password: a special character must occur at least once")
    @Pattern(regexp = "(?=\\S+$).{8,}",message = "password: no whitespace allowed")
    private String password;

    private String verifyPassword;
}
