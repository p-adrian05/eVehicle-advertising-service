package org.example.core.user.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.user.UserAfterCreatedObserver;
import org.example.core.user.UserDataService;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.model.CreatedUserDto;
import org.example.core.user.model.UserDataDto;
import org.example.core.user.persistence.entity.UserDataEntity;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserDataRepository;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService, UserAfterCreatedObserver {

    private final UserDataRepository userDataRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<UserDataDto> getUserData(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        return convertUserDataEntityToDto(userDataRepository.findUserDataEntityByUserEntityUsername(username));
    }

    @Override
    @Transactional
    public void updateUserData(@NonNull UserDataDto userData) throws UnknownUserException {
        Objects.requireNonNull(userData, "UserDataDto cannot be null for updating");
        Objects.requireNonNull(userData.getUsername(), "Username cannot be null for updating user data");
        UserDataEntity userDataEntity = queryUserData(userData.getUsername());
        userDataEntity.setCity(userData.getCity());
        userDataEntity.setFullName(userData.getFullName());
        userDataEntity.setPhoneNumber(userData.getPhoneNumber());
        userDataEntity.setPublicEmail(userData.getPublicEmail());
        userDataRepository.save(userDataEntity);
        log.info("Updated user data: {}",userDataEntity);
    }
    private UserDataDto convertUserDataEntityToUserDataDto(UserDataEntity userDataEntity){
        Objects.requireNonNull(userDataEntity, "UserDataEntity cannot be null for converting");
        return UserDataDto.builder()
            .city(userDataEntity.getCity())
            .fullName(userDataEntity.getFullName())
            .phoneNumber(userDataEntity.getPhoneNumber())
            .publicEmail(userDataEntity.getPublicEmail())
            .build();
    }
    private Optional<UserDataDto> convertUserDataEntityToDto(Optional<UserDataEntity> userDataEntity) {
        return userDataEntity.map(this::convertUserDataEntityToUserDataDto);
    }
    private UserDataEntity queryUserData(String username) throws UnknownUserException {
        Objects.requireNonNull(username, "Username cannot be null for query");
        Optional<UserDataEntity> userDataEntity = userDataRepository.findUserDataEntityByUserEntityUsername(username);
        if(userDataEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried userdata : {}",userDataEntity.get());
        return userDataEntity.get();
    }

    @Override
    public void handleCreatedUser(CreatedUserDto createdUserDto) {
        Objects.requireNonNull(createdUserDto, "CreatedUserDto cannot be null");
        Objects.requireNonNull(createdUserDto.getUserId(), "User id  cannot be null");
        UserDataEntity userDataEntity = new UserDataEntity();
        Optional<UserEntity> userEntity = userRepository.findById(createdUserDto.getUserId());
        if (userEntity.isPresent()) {
            userDataEntity.setUserEntity(userEntity.get());
            userDataRepository.save(userDataEntity);
        }
    }
}
