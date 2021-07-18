package org.example.core.user.impl;

import lombok.RequiredArgsConstructor;
import org.example.core.image.ImageService;
import org.example.core.role.RoleService;
import org.example.core.user.UserRegistrationService;
import org.example.core.user.model.CreateUserDto;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final ImageService imageService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity registerUser(CreateUserDto createUserDto ) {
        String hashedPassword = passwordEncoder.encode(createUserDto.getPassword());
        String activationCode = UUID.randomUUID() + createUserDto.getUsername();

        return UserEntity.builder()
            .username(createUserDto.getUsername())
            .password(hashedPassword)
            .created(new Timestamp(new Date().getTime()))
            .activation(activationCode)
            .email(createUserDto.getEmail())
            .profileImage(imageService.queryDefaultProfileImageEntity())
            .roles(Set.of(roleService.queryDefaultRole()))
            .enabled(false)
            .lastLogin(null)
            .build();
    }

}
