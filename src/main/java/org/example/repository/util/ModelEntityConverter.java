package org.example.repository.util;

import org.example.model.*;
import org.example.repository.entity.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

public class ModelEntityConverter {


    public static User convertUserEntityToUser(UserEntity userEntity){
        return User.builder()
                .username(userEntity.getUsername())
                .created(userEntity.getCreated())
                .activation(userEntity.getActivation())
                .email(userEntity.getEmail())
                .enabled(userEntity.isEnabled())
                .profileImage(convertImageEntityToModel(userEntity.getProfileImage()))
                .lastLogin(userEntity.getLastLogin())
                .build();
    }
    public static UserData convertUserDataFromUserDataEntity(UserDataEntity userDataEntity){
        return UserData.builder()
                .city(userDataEntity.getCity())
                .fullName(userDataEntity.getFullName())
                .phoneNumber(userDataEntity.getPhoneNumber())
                .publicEmail(userDataEntity.getPublicEmail())
                .build();
    }
    public static UserEntity createNewUserEntity(User user){
        return UserEntity.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .created(new Timestamp(new Date().getTime()))
                .activation(user.getActivation())
                .email(user.getEmail())
                .enabled(false)
                .lastLogin(null)
                .build();
    }

    public static ImageEntity convertImageToImageEntity(Image adImage){
        return ImageEntity.builder()
                .id(adImage.getId())
                .path(adImage.getPath())
                .uploadedTime(adImage.getUploadedTime())
                .build();
    }
    public static Image convertImageEntityToModel(ImageEntity imageEntity){
        return Image.builder()
                .id(imageEntity.getId())
                .path(imageEntity.getPath())
                .uploadedTime(imageEntity.getUploadedTime())
                .build();
    }

    public static AdDetails convertBasicAdDetailsEntityToModel(BasicAdDetailsEntity basicAdDetailsEntity){
        return AdDetails.builder()
                .adId(basicAdDetailsEntity.getAdId())
                .year(basicAdDetailsEntity.getYear())
                .batterySize(basicAdDetailsEntity.getBatterySize())
                .chargeSpeed(basicAdDetailsEntity.getChargeSpeed())
                .seatNumber(basicAdDetailsEntity.getSeatNumber())
                .performance(basicAdDetailsEntity.getPerformance())
                .km(basicAdDetailsEntity.getKm())
                .drive(basicAdDetailsEntity.getDrive())
                .build();
    }
    public static AdDetails convertAdDetailsEntityToModel(AdDetailsEntity adDetailsEntity){
        return AdDetails.builder()
                .adId(adDetailsEntity.getAdId())
                .description(adDetailsEntity.getDescription())
                .color(adDetailsEntity.getColor())
                .accelaration(adDetailsEntity.getAccelaration())
                .range(adDetailsEntity.getRange())
                .maxSpeed(adDetailsEntity.getMaxSpeed())
                .weight(adDetailsEntity.getWeight())
                .build();
    }

    public static AdDetailsEntity convertAdDetailsToAdDetailsEntity(AdDetails adDetails){
        return AdDetailsEntity.builder()
                .adId(adDetails.getAdId())
                .maxSpeed(adDetails.getMaxSpeed())
                .range(adDetails.getRange())
                .weight(adDetails.getWeight())
                .accelaration(adDetails.getAccelaration())
                .color(adDetails.getColor())
                .description(adDetails.getDescription())
                .build();
    }
    public static BasicAdDetailsEntity convertAdDetailsToBasicAdDetailsEntity(AdDetails adDetails){
        return BasicAdDetailsEntity.builder()
                .adId(adDetails.getAdId())
                .performance(adDetails.getPerformance())
                .batterySize(adDetails.getBatterySize())
                .km(adDetails.getKm())
                .chargeSpeed(adDetails.getChargeSpeed())
                .drive(adDetails.getDrive())
                .seatNumber(adDetails.getSeatNumber())
                .year(adDetails.getYear())
                .build();
    }

    public static Advertisement convertAdvertisementEntityToModel(AdvertisementEntity advertisementEntity){
        return Advertisement.builder()
                .id(advertisementEntity.getId())
                .price(advertisementEntity.getPrice())
                .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
                .condition(advertisementEntity.getCondition())
                .created(advertisementEntity.getCreated())
                .title(advertisementEntity.getTitle())
                .state(advertisementEntity.getState())
                .type(advertisementEntity.getType().getName())
                .images(advertisementEntity.getImages().stream()
                .map(ModelEntityConverter::convertImageEntityToModel).collect(Collectors.toList()))
                .basicAdDetails(convertBasicAdDetailsEntityToModel(advertisementEntity.getBasicAdDetails()))
                .build();
    }
    public static UserRate convertUserRateEntityToModel(UserRateEntity userRateEntity){
        return UserRate.builder()
                .id(userRateEntity.getRateId())
                .ratingUsername(userRateEntity.getRatingUser().getUsername())
                .ratedUsername(userRateEntity.getRatedUser().getUsername())
                .advertisement(Advertisement.builder()
                        .id(userRateEntity.getAdvertisement().getId())
                        .title(userRateEntity.getAdvertisement().getTitle())
                        .build())
                .created(userRateEntity.getRate().getCreated())
                .ratingUserProfileImageId(userRateEntity.getRatingUser().getProfileImage().getId())
                .description(userRateEntity.getRate().getDescription())
                .ratedState(userRateEntity.getState())
                .rateState(userRateEntity.getRate().getState())
                .activationCode(userRateEntity.getActivationCode())
                .status(userRateEntity.getStatus())
                .build();
    }

    public static RateEntity createNewRateEntity(UserRate userRate){
        return RateEntity.builder()
                .description(userRate.getDescription())
                .created(new Timestamp(new Date().getTime()))
                .state(userRate.getRateState())
                .build();
    }
}
