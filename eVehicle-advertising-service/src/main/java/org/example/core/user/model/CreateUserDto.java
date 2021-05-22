package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class CreateUserDto {

    private final String username;

    private final String password;

    private final String email;
}
