package org.example.core.user;


import org.example.security.exception.AuthException;

public interface LoginService {

    String login(String username, String password) throws AuthException;

    void activateUser(String code) throws AuthException;

}
