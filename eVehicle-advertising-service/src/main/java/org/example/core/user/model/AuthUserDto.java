package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.core.role.model.Role;

import java.util.List;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@ToString
public class AuthUserDto {

    private final String username;

    private final String password;

    private final String email;

    private final boolean enabled;

    private final List<String> roles;
}
