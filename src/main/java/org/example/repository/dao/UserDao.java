package org.example.repository.dao;

import lombok.NonNull;
import org.example.exceptions.*;
import org.example.model.User;
import org.example.model.UserData;

import java.util.List;

public interface UserDao {

    int createUser(User user) throws UsernameAlreadyExistsException, UnknownRoleException, EmailAlreadyExistsException;

    void deleteUser(String username) throws UnknownUserException;

    void updateUser(User user) throws UnknownUserException;

    User getUserByName(String username) throws UnknownUserException;

    void updateUsername(String oldUsername,String newUsername) throws UsernameAlreadyExistsException, UnknownUserException;

    UserData getUserData(String username) throws UnknownUserException;

    void updateUserData(UserData userData) throws UnknownUserException;

    void addSaveAd(String username,int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException;

    void removeSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException;

    void addRole(String username, List<String> roles) throws UnknownUserException, UnknownRoleException;

    void removeRole(String username, List<String> roles) throws UnknownUserException, UnknownRoleException;

    User getUserByNameWithRoles(String username) throws UnknownUserException;
    void activateUser(User user) throws UnknownUserException;
    User getUserByActivationCode(String code) throws UnknownUserException;
}
