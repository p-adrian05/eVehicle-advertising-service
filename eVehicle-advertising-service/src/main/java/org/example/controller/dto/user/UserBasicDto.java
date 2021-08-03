package org.example.controller.dto.user;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class UserBasicDto{

    private String username;

    private boolean enabled;

    private Timestamp lastLogin;

    private Timestamp created;

    private String profileImagePath;
}
