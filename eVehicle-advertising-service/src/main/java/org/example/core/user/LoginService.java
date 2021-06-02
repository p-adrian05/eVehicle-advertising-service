package org.example.core.user;


import org.example.core.security.AuthException;

public interface LoginService {

    String login(String username, String password) throws AuthException;

    void activateUser(String code) throws AuthException;

}
