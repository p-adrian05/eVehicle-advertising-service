package org.example.core.user.impl;

import lombok.RequiredArgsConstructor;
import org.example.core.user.model.CreatedUserDto;
import org.example.core.user.UserAfterCreatedObserver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAfterCreatedObservable {

    private final List<UserAfterCreatedObserver> observers;

    public void broadCastUser(CreatedUserDto createdUserDto){
        Objects.requireNonNull(createdUserDto, "CreatedUserDto cannot be null");
        observers.forEach(observer->observer.handleCreatedUser(createdUserDto));
    }
}
