package org.example.services;

import org.example.exceptions.*;
import org.example.model.User;
import org.example.model.UserData;

import java.util.List;

public interface UserService {

    void createUser(User user) throws UsernameAlreadyExistsException, UnknownRoleException, EmailAlreadyExistsException;

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

    List<String> getRoles(String username) throws UnknownUserException;

    public String login(String username, String password) throws AuthException;

    void activateUser(String code);
}
