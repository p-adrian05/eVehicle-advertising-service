package org.example.core.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.core.user.persistence.entity.UserDataEntity;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UserDataDto {

    private String username;

    private String city;

    private String fullName;

    private String publicEmail;

    private String phoneNumber;

    public static UserDataDto of(@NonNull UserDataEntity userDataEntity){
        return UserDataDto.builder()
                .city(userDataEntity.getCity())
                .fullName(userDataEntity.getFullName())
                .phoneNumber(userDataEntity.getPhoneNumber())
                .publicEmail(userDataEntity.getPublicEmail())
                .build();
    }
}
