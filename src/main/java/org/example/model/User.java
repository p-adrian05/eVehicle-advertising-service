package org.example.model;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

import java.util.List;


@Data
@Builder
public class User {

    private String username;

    private String password;

    private String email;

    private boolean enabled;

    private String activation;

    private Timestamp lastLogin;

    private Timestamp created;

    private Image profileImage;

    private List<String> roles;

    private List<Advertisement> favAds;
}
