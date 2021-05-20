package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exceptions.UnknownUserException;
import org.example.model.User;
import org.example.repository.dao.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userDao.getUserByNameWithRoles(username);
            return new UserDetailsImpl(user);
        } catch (UnknownUserException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
