package org.example.core.user;

import javax.security.auth.message.AuthException;

public interface LoginService {

    String login(String username, String password) throws AuthException;

    void activateUser(String code);
}
