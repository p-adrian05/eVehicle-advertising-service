package org.example.core.user;

import org.example.core.user.persistence.entity.UserEntity;

public interface UserCreateObserver {

    void handleNewUser(UserEntity userEntity);
}
