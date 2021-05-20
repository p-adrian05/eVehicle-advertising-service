package org.example.repository.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.*;
import org.example.model.User;
import org.example.model.UserData;
import org.example.repository.RoleRepository;
import org.example.repository.UserDataRepository;
import org.example.repository.UserRepository;
import org.example.repository.entity.*;
import org.example.repository.util.ModelEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@Repository
public class UserDaoImpl implements UserDao{

    private final UserRepository userRepository;
    private final EntityQuery entityQuery;
    private final UserDataRepository userDataRepository;
    private final RoleRepository roleRepository;

    @Value("${user.default-role:USER}")
    private String DEFAULT_ROLE;

    @Override
    @Transactional
    public int createUser(@NonNull User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, UnknownRoleException {
        if(userRepository.existsUserEntityByUsername(user.getUsername())){
           throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",user.getUsername()));
        }
        if(userRepository.existsUserEntityByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException(String.format("Email already exists: %s",user.getEmail()));
        }
        UserEntity userEntity =  ModelEntityConverter.createNewUserEntity(user);
        userEntity.addRole(entityQuery.queryRoleByRoleName(DEFAULT_ROLE));
        try {
            ImageEntity defImage = entityQuery.queryDefaultProfileImage();
            userEntity.setProfileImage(defImage);
        } catch (UnknownImageException e) {
            log.error("Failed to query default profile image");
            userEntity.setProfileImage(null);
        }
        UserEntity savedUser = userRepository.save(userEntity);
        UserDataEntity userData = new UserDataEntity();
        userData.setUserEntity(savedUser);
        userDataRepository.save(userData);
        log.info("Created user entity: {}",userEntity);
        return savedUser.getId();
    }

    @Override
    @Transactional
    public void deleteUser(String username) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntity(username);
        userEntity.setEnabled(false);
        userRepository.save(userEntity);
        log.info("Disabled user entity: {}",userEntity);
    }

    @Override
    @Transactional
    public void updateUser(@NonNull User user) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntity(user.getUsername());
        userEntity.setLastLogin(user.getLastLogin());
        log.info("Updated user entity: {}",userEntity);
        userRepository.save(userEntity);
    }
    @Override
    @Transactional
    public void activateUser(@NonNull User user) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntity(user.getUsername());
        userEntity.setActivation(user.getActivation());
        userEntity.setEnabled(user.isEnabled());
        log.info("Activated user entity: {}",userEntity);
        userRepository.save(userEntity);
    }
    @Override
    @Transactional
    public void addSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException {
        UserEntity userEntity = entityQuery.queryUserEntityWithSavesAds(username);
        log.info("Adding new saved Ad to user with username: {}",username);
        modifySaveAds(userEntity,adId,userEntity::addSavedAd);
    }
    @Override
    @Transactional
    public void addRole(String username, List<String> roles) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntityWithRoles(username);
        log.info("Adding role to user with username: {}",username);
        log.info("User roles: {}",userEntity.getRoles());
        Collection<RoleEntity> roleEntities = roleRepository.findRoleEntitiesByRoleNameIsIn(roles);
        for(RoleEntity role: roleEntities){
            if(!role.getRoleName().equals(DEFAULT_ROLE)){
                userEntity.addRole(role);
            }
        }
        userRepository.save(userEntity);
        log.info("User roles after modification : {}",userEntity.getRoles());
    }
    @Override
    @Transactional
    public void removeRole(String username, List<String> roles) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntityWithRoles(username);
        log.info("Removing role from user with username: {}",username);
        log.info("User roles: {}",userEntity.getRoles());
        Collection<RoleEntity> roleEntities = roleRepository.findRoleEntitiesByRoleNameIsIn(roles);
        log.info("Removing roles: {}",roleEntities);
        log.info("Removing roles: {}",roles);
        for(RoleEntity role: roleEntities){
            if(!role.getRoleName().equals(DEFAULT_ROLE)){
               userEntity.removeRole(role);
            }
        }
        userRepository.save(userEntity);
        log.info("User roles after modification : {}",userEntity.getRoles());
    }
    @Override
    @Transactional
    public void removeSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException {
        UserEntity userEntity = entityQuery.queryUserEntityWithSavesAds(username);
        log.info("Removing saved Ad from user with username: {}",username);
        modifySaveAds(userEntity,adId,userEntity::removeSavedAd);
    }
    @Transactional
    @Override
    public void updateUsername(String oldUsername,String newUsername) throws UsernameAlreadyExistsException, UnknownUserException {
        if(userRepository.existsUserEntityByUsername(newUsername)){
            throw new UsernameAlreadyExistsException(String.format("Username already exists: %s",newUsername));
        }
        UserEntity userEntity = entityQuery.queryUserEntity(oldUsername);
        log.info("old/new usernames: {}/{}",oldUsername,newUsername);
        userEntity.setUsername(newUsername);
        userRepository.save(userEntity);
    }

    @Override
    public UserData getUserData(String username) throws UnknownUserException {
        UserData userData = UserData.of(entityQuery.queryUserData(username));
        userData.setUsername(username);
        log.info("Queried user data: {}",userData);
        return userData;
    }

    @Override
    @Transactional
    public void updateUserData(@NonNull UserData userData) throws UnknownUserException {
        UserDataEntity userDataEntity = entityQuery.queryUserData(userData.getUsername());
        userDataEntity.setCity(userData.getCity());
        userDataEntity.setFullName(userData.getFullName());
        userDataEntity.setPhoneNumber(userData.getPhoneNumber());
        userDataEntity.setPublicEmail(userData.getPublicEmail());
        userDataRepository.save(userDataEntity);
        log.info("Updated user data: {}",userDataEntity);
    }

    @Override
    public User getUserByName(String username) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntityWithImage(username);
        return ModelEntityConverter.convertUserEntityToUser(userEntity);
    }
    @Override
    public User getUserByActivationCode(String code) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntityByCode(code);
        return ModelEntityConverter.convertUserEntityToUser(userEntity);
    }
    @Override
    public User getUserByNameWithRoles(String username) throws UnknownUserException {
        UserEntity userEntity = entityQuery.queryUserEntityWithRoles(username);
        return User.builder()
                .username(userEntity.getUsername())
                .created(userEntity.getCreated())
                .activation(userEntity.getActivation())
                .email(userEntity.getEmail())
                .enabled(userEntity.isEnabled())
                .password(userEntity.getPassword())
                .roles(userEntity.getRoles().stream().map(RoleEntity::getRoleName).collect(Collectors.toList()))
                .build();
    }
    protected void modifySaveAds(UserEntity userEntity,int adId,Consumer<AdvertisementEntity> saveAdsModifier) throws UnknownAdvertisementException, MaximumSavedAdsReachedException {
        AdvertisementEntity advertisementEntity =  entityQuery.queryAdvertisement(adId);
        log.info("User saved ads: {}",userEntity.getSavedAds());
        if(userEntity.getSavedAds().size()==15){
            throw new MaximumSavedAdsReachedException(String.format("Maximum saved ads 15 reached for user: %s",userEntity.getUsername()));
        }
        saveAdsModifier.accept(advertisementEntity);
        log.info("Modified advertisement: {}",advertisementEntity);
        log.info("User saved ads after modification : {}",userEntity.getSavedAds());
        userRepository.save(userEntity);
    }

}
