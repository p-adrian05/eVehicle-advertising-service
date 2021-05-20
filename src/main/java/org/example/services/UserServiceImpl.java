package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.exceptions.*;
import org.example.model.User;
import org.example.model.UserData;
import org.example.repository.dao.UserDao;
import org.example.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final EmailService emailService;

    @Override
    @Transactional
    public void createUser(User user) throws UsernameAlreadyExistsException, UnknownRoleException, EmailAlreadyExistsException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String code = UUID.randomUUID().toString()+user.getUsername();
        user.setActivation(code);
        userDao.createUser(user);
        emailService.sendMessage(user.getEmail(),user.getUsername(),code);
    }

    @Override
    public void deleteUser(String username) throws UnknownUserException {
        userDao.deleteUser(username);
    }

    @Override
    public void updateUser(User user) throws UnknownUserException {
        userDao.updateUser(user);
    }

    @Override
    public User getUserByName(String username) throws UnknownUserException {
        return userDao.getUserByName(username);
    }

    @Override
    public void updateUsername(String oldUsername, String newUsername) throws UsernameAlreadyExistsException, UnknownUserException {
        userDao.updateUsername(oldUsername,newUsername);
    }

    @Override
    public UserData getUserData(String username) throws UnknownUserException {
        return userDao.getUserData(username);
    }

    @Override
    public void updateUserData(UserData userData) throws UnknownUserException {
        userDao.updateUserData(userData);
    }

    @Override
    public void addSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException {
        userDao.addSaveAd(username,adId);
    }

    @Override
    public void removeSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException {
        userDao.removeSaveAd(username,adId);
    }

    @Override
    public void addRole(String username, List<String> roles) throws UnknownUserException, UnknownRoleException {
        userDao.addRole(username,roles);
    }

    @Override
    public void removeRole(String username, List<String> roles) throws UnknownUserException, UnknownRoleException {
        userDao.removeRole(username,roles);
    }
    @Override
    public List<String> getRoles(String username) throws UnknownUserException {
       return userDao.getUserByNameWithRoles(username).getRoles();
    }
    @Override
    public String login(String username, String password) throws AuthException {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            User user = userDao.getUserByName(authentication.getName());
            user.setLastLogin(new Timestamp(new Date().getTime()));
            userDao.updateUser(user);
            return jwtTokenProvider.createToken(authentication);
        }catch (AuthenticationException | UnknownUserException e){
            throw new AuthException("Incorrect username or password");
        }
    }
    @Override
    public void activateUser(String code) {
        try {
            User user = userDao.getUserByActivationCode(code);
            user.setEnabled(true);
            user.setActivation(null);
            userDao.activateUser(user);
        } catch (UnknownUserException e) {
            throw new AuthException("Invalid activation code");
        }
    }
}
