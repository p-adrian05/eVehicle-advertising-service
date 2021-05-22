package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UserDataDto {

    private final String username;

    private final String city;

    private final String fullName;

    private final String publicEmail;

    private final String phoneNumber;
}
