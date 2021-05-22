package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class AuthUserDto {

    private final String username;

    private final String password;

    private final String email;

    private final boolean enabled;

    private final List<String> roles;
}
