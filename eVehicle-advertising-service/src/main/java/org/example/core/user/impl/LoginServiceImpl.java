package org.example.core.user.impl;

import org.example.core.user.LoginService;
import org.example.core.user.UserService;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.model.UserDto;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.security.auth.message.AuthException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public String login(String username, String password) throws AuthException {
        Optional<UserDto> user = userService.getUserByName(username);
        if(user.isEmpty()){
            throw new AuthException("Incorrect username or password");
        }
        try{
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
            //user.setLastLogin(new Timestamp(new Date().getTime()));
           // userDao.updateUser(user);
            return jwtTokenProvider.createToken(authentication);
        }catch (AuthenticationException | UnknownUserException e){
            throw new AuthException("Incorrect username or password");
        }
    }
    @Override
    public void activateUser(String code) {
        try {
            User user = userDao.getUserByActivationCode(code);
            Optional<UserEntity> userEntity  = userRepository.findByActivation(code);
            if(userEntity.isEmpty()){
                throw new AuthException("Invalid activation code");
            }
            userEntity.get().setActivation(null);
            userEntity.get().setEnabled(true);
            userRepository.save(userEntity.get());
        } catch (UnknownUserException e) {
            throw new AuthException("Invalid activation code");
        }
    }
}
