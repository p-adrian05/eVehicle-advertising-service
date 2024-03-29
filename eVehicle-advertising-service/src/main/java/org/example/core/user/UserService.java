package org.example.core.user;

import org.example.core.role.exception.UnknownRoleException;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.core.user.model.CreateUserDto;
import org.example.core.user.model.UserDto;

import java.util.Optional;


public interface UserService {

    int createUser(CreateUserDto user) throws UsernameAlreadyExistsException, UnknownRoleException, EmailAlreadyExistsException;

    void deleteUser(String username) throws UnknownUserException;

    Optional<UserDto> getUserByName(String username);

    void updateUsername(String oldUsername,String newUsername) throws UsernameAlreadyExistsException, UnknownUserException;

    Optional<UserDto> getUserByActivationCode(String code) throws UnknownUserException;

}
