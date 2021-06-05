package org.example.config;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.image.persistence.persistence.ImageRepository;
import org.example.core.role.model.Role;
import org.example.core.role.persistence.entity.RoleEntity;
import org.example.core.role.persistence.repository.RoleRepository;
import org.example.core.user.persistence.entity.UserDataEntity;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserDataRepository;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;


@Component
@Generated
@Profile("! prod")
@RequiredArgsConstructor
public class InMemoryDbInitializer {

    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final RoleRepository roleRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        RoleEntity roleEntity = RoleEntity.builder()
            .roleName(Role.ADMIN)
            .build();
        roleEntity = roleRepository.save(roleEntity);
        ImageEntity imageEntity = ImageEntity.builder()
            .path("profiles/default.jpg")
            .build();
        imageRepository.save(imageEntity);
       UserEntity userEntity = UserEntity.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles(Set.of(roleEntity))
            .email("testemail")
            .created(new Timestamp(new Date().getTime()))
           .profileImage(imageEntity)
            .enabled(true)
            .build();
       userEntity = userRepository.save(userEntity);;
        UserDataEntity userDataEntity = UserDataEntity.builder()
            .userId(userEntity.getId())
            .userEntity(userEntity)
            .city("Test city")
            .fullName("Admin test")
            .build();
        userDataRepository.save(userDataEntity);

        CategoryEntity categoryEntity = CategoryEntity.builder()
            .name("Car")
            .build();
        categoryRepository.save(categoryEntity);



    }

}
