package org.example.core.user.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.image.model.ImageDto;
import org.example.core.user.UserService;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.core.user.model.CreateUserDto;
import org.example.core.user.model.CreatedUserDto;
import org.example.core.user.model.UserDto;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCreateObservable userCreateObservable;
    private final UserAfterCreatedObservable userAfterCreatedObservable;

    @Override
    @Transactional
    public void createUser(@NonNull CreateUserDto createUserDto) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Objects.requireNonNull(createUserDto, "CreatUserDto cannot be null for creating user");
        Objects.requireNonNull(createUserDto.getEmail(), "Email cannot be null for creating user");
        Objects.requireNonNull(createUserDto.getUsername(), "Username cannot be null for creating user");
        Objects.requireNonNull(createUserDto.getPassword(), "Password cannot be null for creating user");
        if(userRepository.existsUserEntityByUsername(createUserDto.getUsername())){
            throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",createUserDto.getUsername()));
        }
        if(userRepository.existsUserEntityByEmail(createUserDto.getEmail())){
            throw new EmailAlreadyExistsException(String.format("Email already exists: %s",createUserDto.getEmail()));
        }
        UserEntity userEntity = createNewUserEntity(createUserDto);
        userCreateObservable.broadCastUser(userEntity);
        UserEntity savedUserEntity  = userRepository.save(userEntity);
        userAfterCreatedObservable.broadCastUser(CreatedUserDto.builder()
            .userId(savedUserEntity.getId())
            .activationCode(savedUserEntity.getActivation())
            .email(savedUserEntity.getEmail())
            .username(savedUserEntity.getUsername())
            .build());
        log.info("Created user entity: {}",userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(String username) throws UnknownUserException {
        Objects.requireNonNull(username, "Username cannot be null for deleting user");
        UserEntity userEntity = queryUserEntity(username);
        userEntity.setEnabled(false);
        userRepository.save(userEntity);
        log.info("Disabled user entity: {}",userEntity);
    }
    @Transactional
    @Override
    public void updateUsername(String oldUsername,String newUsername) throws UsernameAlreadyExistsException, UnknownUserException {
        Objects.requireNonNull(oldUsername, "Old username cannot be null for updating username");
        Objects.requireNonNull(newUsername, "New username cannot be null for updating username");
        if(userRepository.existsUserEntityByUsername(newUsername)){
            throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",newUsername));
        }
        UserEntity userEntity = queryUserEntity(oldUsername);
        log.info("old/new usernames: {}/{}",oldUsername,newUsername);
        userEntity.setUsername(newUsername);
        userRepository.save(userEntity);
    }

    @Override
    public Optional<UserDto> getUserByName(String username){
        Objects.requireNonNull(username, "Username cannot be null");
        return convertUserEntityToDto(userRepository.findUserWithRolesAndImage(username));
    }
    @Override
    public Optional<UserDto> getUserByActivationCode(String code) {
        Objects.requireNonNull(code, "Activation code cannot be null");
        return convertUserEntityToDto(userRepository.findByActivation(code));
    }

    public UserDto convertUserEntityToUserDto(UserEntity userEntity){
        Objects.requireNonNull(userEntity, "UserEntity code cannot be null for converting");
        return UserDto.builder()
            .username(userEntity.getUsername())
            .created(userEntity.getCreated())
            .email(userEntity.getEmail())
            .enabled(userEntity.isEnabled())
            .profileImage(ImageDto.builder()
                .id(userEntity.getProfileImage().getId())
                .path(userEntity.getProfileImage().getPath())
                .uploadedTime(userEntity.getProfileImage().getUploadedTime())
                .build())
            .lastLogin(userEntity.getLastLogin())
            .build();
    }


    private UserEntity createNewUserEntity(CreateUserDto createUserDto) {
       String hashedPassword = passwordEncoder.encode(createUserDto.getPassword());
       String activationCode = UUID.randomUUID() + createUserDto.getUsername();
        return UserEntity.builder()
             .username(createUserDto.getUsername())
             .password(hashedPassword)
             .created(new Timestamp(new Date().getTime()))
             .activation(activationCode)
             .email(createUserDto.getEmail())
             .enabled(false)
             .lastLogin(null)
             .build();
    }
    private Optional<UserDto> convertUserEntityToDto(Optional<UserEntity> userEntity) {
        return userEntity.map(this::convertUserEntityToUserDto);
    }

    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if(userEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried user : {}",userEntity.get());
        return userEntity.get();
    }
}
