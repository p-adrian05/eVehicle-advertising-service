package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.core.image.model.ImageDto;
import org.example.core.role.model.Role;

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

    private final ImageDto profileImage;

    private final List<Role> roles;
}
