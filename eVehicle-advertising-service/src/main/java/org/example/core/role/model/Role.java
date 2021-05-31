package org.example.core.role.model;

public enum Role {
    ADMIN,USER;

    public static Role defaultRole(){
        return Role.USER;
    }
}
