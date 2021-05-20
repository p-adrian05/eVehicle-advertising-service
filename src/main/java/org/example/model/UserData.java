package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.example.repository.entity.UserDataEntity;

@Data
@Builder
public class UserData {

    private String username;

    private String city;

    private String fullName;

    private String publicEmail;

    private String phoneNumber;

    public static UserData of(@NonNull UserDataEntity userDataEntity){
        return UserData.builder()
                .city(userDataEntity.getCity())
                .fullName(userDataEntity.getFullName())
                .phoneNumber(userDataEntity.getPhoneNumber())
                .publicEmail(userDataEntity.getPublicEmail())
                .build();
    }
}
