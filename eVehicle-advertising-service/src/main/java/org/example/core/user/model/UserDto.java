package org.example.core.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.Image;
import java.sql.Timestamp;
import java.util.List;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UserDto {

    private final String username;

    private final String email;

    private final boolean enabled;

    private final Timestamp lastLogin;

    private final Timestamp created;

    private final Image profileImage;

    private final List<String> roles;
}
