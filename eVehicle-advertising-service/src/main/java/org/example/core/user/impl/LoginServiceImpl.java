package org.example.core.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.security.JwtTokenProvider;
import org.example.core.user.LoginService;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.security.auth.message.AuthException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public String login(String username, String password) throws AuthException {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new AuthException("Incorrect username or password");
        }
        try {
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            user.get().setLastLogin(new Timestamp(new Date().getTime()));
            userRepository.save(user.get());
            return jwtTokenProvider.createToken(authentication);
        } catch (AuthenticationException e) {
            throw new AuthException("Incorrect username or password");
        }
    }

    @Override
    public void activateUser(String code) throws AuthException {
        Optional<UserEntity> userEntity = userRepository.findByActivation(code);
        if (userEntity.isEmpty()) {
            throw new AuthException("Invalid activation code");
        }
        userEntity.get().setActivation(null);
        userEntity.get().setEnabled(true);
        userRepository.save(userEntity.get());
    }


}
