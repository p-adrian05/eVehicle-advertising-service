package org.example.core.user;

import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.model.UserDataDto;

import java.util.Optional;

public interface UserDataService {

    Optional<UserDataDto> getUserData(String username);

    void updateUserData(UserDataDto userData) throws UnknownUserException;
}
