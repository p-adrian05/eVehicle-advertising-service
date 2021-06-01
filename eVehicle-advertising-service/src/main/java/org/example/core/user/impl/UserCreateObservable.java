package org.example.core.user.impl;

import lombok.RequiredArgsConstructor;
import org.example.core.user.UserCreateObserver;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserCreateObservable {

    private final List<UserCreateObserver> observers;

    public void broadCastUser(UserEntity userEntity){
        Objects.requireNonNull(userEntity, "UserEntity cannot be null");
        observers.forEach(observer->observer.handleNewUser(userEntity));
    }
}
