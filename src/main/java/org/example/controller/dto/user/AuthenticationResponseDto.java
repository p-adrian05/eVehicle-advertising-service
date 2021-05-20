package org.example.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationResponseDto {

    private final String jwt;
}
