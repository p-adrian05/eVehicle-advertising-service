package org.example.core.user.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.user.UserService;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownRoleException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.core.user.model.CreateUserDto;
import org.example.core.user.model.UserDataDto;
import org.example.core.user.model.UserDto;
import org.example.core.user.persistence.entity.UserDataEntity;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserDataRepository;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.Image;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.default-role:USER}")
    private String DEFAULT_ROLE;

    @Override
    @Transactional
    public void createUser(@NonNull CreateUserDto createUserDto) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UnknownRoleException {
        if(userRepository.existsUserEntityByUsername(createUserDto.getUsername())){
            throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",createUserDto.getUsername()));
        }
        if(userRepository.existsUserEntityByEmail(createUserDto.getEmail())){
            throw new EmailAlreadyExistsException(String.format("Email already exists: %s",createUserDto.getEmail()));
        }
        UserEntity userEntity =  createNewUserEntity(createUserDto);
        UserEntity savedUser = userRepository.save(userEntity);
        UserDataEntity userData = new UserDataEntity();
        userData.setUserEntity(savedUser);
        userDataRepository.save(userData);
        log.info("Created user entity: {}",userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(String username) throws UnknownUserException {
        UserEntity userEntity = queryUserEntity(username);
        userEntity.setEnabled(false);
        userRepository.save(userEntity);
        log.info("Disabled user entity: {}",userEntity);
    }
    @Transactional
    @Override
    public void updateUsername(String oldUsername,String newUsername) throws UsernameAlreadyExistsException, UnknownUserException {
        if(userRepository.existsUserEntityByUsername(newUsername)){
            throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",newUsername));
        }
        UserEntity userEntity = queryUserEntity(oldUsername);
        log.info("old/new usernames: {}/{}",oldUsername,newUsername);
        userEntity.setUsername(newUsername);
        userRepository.save(userEntity);
    }

    @Override
    public Optional<UserDataDto> getUserData(String username) {
       return convertUserDataEntityToDto(userDataRepository.findUserDataEntityByUserEntityUsername(username));
    }

    @Override
    @Transactional
    public void updateUserData(@NonNull UserDataDto userData) throws UnknownUserException {
        UserDataEntity userDataEntity = queryUserData(userData.getUsername());
        userDataEntity.setCity(userData.getCity());
        userDataEntity.setFullName(userData.getFullName());
        userDataEntity.setPhoneNumber(userData.getPhoneNumber());
        userDataEntity.setPublicEmail(userData.getPublicEmail());
        userDataRepository.save(userDataEntity);
        log.info("Updated user data: {}",userDataEntity);
    }

    @Override
    public Optional<UserDto> getUserByName(String username){
        return convertUserEntityToDto(userRepository.findUserEntityByUsername(username));
    }
    @Override
    public Optional<UserDto> getUserByActivationCode(String code) {
        return convertUserEntityToDto(userRepository.findByActivation(code));
    }

    public UserDto convertUserEntityToUserDto(UserEntity userEntity){
        return UserDto.builder()
            .username(userEntity.getUsername())
            .created(userEntity.getCreated())
            .email(userEntity.getEmail())
            .enabled(userEntity.isEnabled())
            .profileImage(Image.builder()
                .id(userEntity.getProfileImage().getId())
                .path(userEntity.getProfileImage().getPath())
                .uploadedTime(userEntity.getProfileImage().getUploadedTime())
                .build())
            .lastLogin(userEntity.getLastLogin())
            .build();
    }
//todo
    private ImageEntity queryDefaultProfileImageEntity(){
        return null;
    }

    public UserDataDto convertUserDataEntityToUserDataDto(UserDataEntity userDataEntity){
        return UserDataDto.builder()
            .city(userDataEntity.getCity())
            .fullName(userDataEntity.getFullName())
            .phoneNumber(userDataEntity.getPhoneNumber())
            .publicEmail(userDataEntity.getPublicEmail())
            .build();
    }
    private UserEntity createNewUserEntity(CreateUserDto createUserDto) throws UnknownRoleException {
       String hashedPassword = passwordEncoder.encode(createUserDto.getPassword());
       String activationCode = UUID.randomUUID() + createUserDto.getUsername();
       UserEntity userEntity =  UserEntity.builder()
            .username(createUserDto.getUsername())
            .password(hashedPassword)
            .created(new Timestamp(new Date().getTime()))
            .activation(activationCode)
            .email(createUserDto.getEmail())
            .enabled(false)
            .lastLogin(null)
            .profileImage(queryDefaultProfileImageEntity())
            .build();
        userEntity.addRole(queryRoleByRoleName(DEFAULT_ROLE));
        return userEntity;
    }
    private Optional<UserDto> convertUserEntityToDto(Optional<UserEntity> userEntity) {
        return userEntity.map(this::convertUserEntityToUserDto);
    }
    private Optional<UserDataDto> convertUserDataEntityToDto(Optional<UserDataEntity> userDataEntity) {
        return userDataEntity.map(this::convertUserDataEntityToUserDataDto);
    }
    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if(userEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried user : {}",userEntity.get());
        return userEntity.get();
    }
    public UserDataEntity queryUserData(String username) throws UnknownUserException {
        Optional<UserDataEntity> userDataEntity = userDataRepository.findUserDataEntityByUserEntityUsername(username);
        if(userDataEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried userdata : {}",userDataEntity.get());
        return userDataEntity.get();
    }
    //todo
    public RoleEntity queryRoleByRoleName(String roleName) throws UnknownRoleException {
        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityByRoleName(roleName);
        if(roleEntity.isEmpty()){
            throw new UnknownRoleException(String.format("Role not found: %s",roleName));
        }
        return roleEntity.get();
    }
}
