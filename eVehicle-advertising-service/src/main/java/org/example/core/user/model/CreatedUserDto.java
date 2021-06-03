package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@ToString
public class CreatedUserDto {

    private final Integer userId;
    private final String username;
    private final String email;
    private final String activationCode;
}
