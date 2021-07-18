package org.example.core.user;

import org.example.core.user.model.CreateUserDto;
import org.example.core.user.persistence.entity.UserEntity;

public interface UserRegistrationService {

    UserEntity registerUser(CreateUserDto createUserDto);
}
