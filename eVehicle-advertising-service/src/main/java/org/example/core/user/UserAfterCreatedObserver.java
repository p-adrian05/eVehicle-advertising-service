package org.example.core.user;

import org.example.core.user.model.CreatedUserDto;

public interface UserAfterCreatedObserver {

    void handleCreatedUser(CreatedUserDto createdUserDto);
}
