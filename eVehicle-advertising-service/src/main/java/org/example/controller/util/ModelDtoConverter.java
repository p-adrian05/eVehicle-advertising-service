package org.example.controller.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.example.controller.dto.advertisement.AdvertisementAllDataDto;
import org.example.controller.dto.advertisement.AdvertisementBasicDetailsDto;
import org.example.controller.dto.advertisement.AdvertisementDetailsDto;

import org.example.controller.dto.advertisement.CreateAdvertisementDto;
import org.example.controller.dto.message.MessageDto;
import org.example.controller.dto.rate.CreateUserRateDto;
import org.example.controller.dto.rate.RateAdvertisementDto;
import org.example.controller.dto.rate.UserRateBasicDto;
import org.example.controller.dto.rate.UserRateDto;
import org.example.controller.dto.user.UserBasicDto;
import org.example.controller.dto.user.UserDataDto;
import org.example.controller.dto.user.UserRegistrationDto;

import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.user.model.UserDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.ObjectError;

import java.awt.Image;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelDtoConverter {



    public static UserBasicDto convertUserToUserBasicDto(UserDto user){
        return UserBasicDto.builder()
                .username(user.getUsername())
                .created(user.getCreated())
                .enabled(user.isEnabled())
                .profileImagePath(user.getProfileImage().getPath())
                .lastLogin(user.getLastLogin())
                .build();
    }
    public static UserDataDto convertUserDataToUserDataDto(UserDataDto userData){
        return UserDataDto.builder()
                .username(userData.getUsername())
                .city(userData.getCity())
                .fullName(userData.getFullName())
                .publicEmail(userData.getPublicEmail())
                .phoneNumber(userData.getPhoneNumber())
                .build();
    }
    public static UserDataDto convertUserDataDtoToUserData(UserDataDto userDataDto){
         return  UserDataDto.builder()
                    .username(userDataDto.getUsername())
                    .city(userDataDto.getCity())
                    .fullName(userDataDto.getFullName())
                    .publicEmail(userDataDto.getPublicEmail())
                    .phoneNumber(userDataDto.getPhoneNumber())
                    .build();
    }
//    public static UserRegistrationDto convertUserRegistrationDtoToUser(UserRegistrationDto userRegistrationDto){
//        return UserRegistrationDto.builder()
//                .username(userRegistrationDto.getUsername())
//                .email(userRegistrationDto.getEmail())
//                .password(userRegistrationDto.getPassword())
//                .build();
//    }

    public static List<String> convertBindingErrorsToString(List<ObjectError> errors){
        return errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }

    public static UserRateDto convertUserRateModelToDto(UserRateDto userRate){
        return UserRateDto.builder()
                .advertisement(RateAdvertisementDto.builder()
                                    .id(userRate.getAdvertisement().getId())
                                    .price(userRate.getAdvertisement().getPrice())
                                    .title(userRate.getAdvertisement().getTitle())
                                    .build())
                .created(userRate.getCreated())
                .description(userRate.getDescription())
                .id(userRate.getId())
                .ratedState(userRate.getRatedState())
                .rateState(userRate.getRateState())
                .ratedUsername(userRate.getRatedUsername())
                .ratingUsername(userRate.getRatingUsername())
                .ratingUserProfileImageId(userRate.getRatingUserProfileImageId())
                .build();
    }
    public static UserRateBasicDto convertUserRateModelToBasicDto(UserRateDto userRate){
        return UserRateBasicDto.builder()
                .advertisement(RateAdvertisementDto.builder()
                        .id(userRate.getAdvertisement().getId())
                        .price(userRate.getAdvertisement().getPrice())
                        .title(userRate.getAdvertisement().getTitle())
                        .build())
                .created(userRate.getCreated())
                .id(userRate.getId())
                .ratedState(userRate.getRatedState())
                .ratedUsername(userRate.getRatedUsername())
                .ratingUsername(userRate.getRatingUsername())
                .ratingUserProfileImageId(userRate.getRatingUserProfileImageId())
                .build();
    }
    public static UserRateDto convertCreateUserRateDtoToModel(CreateUserRateDto createUserRateDto){
        return UserRateDto.builder()
                .rateState(createUserRateDto.getRateState())
                .advertisement(RateAdvertisementDto.builder().id(createUserRateDto.getAdId()).build())
                .description(createUserRateDto.getDescription())
                .ratedUsername(createUserRateDto.getRatedUsername())
                .ratingUsername(createUserRateDto.getRatingUsername())
                .build();
    }

    public static MessageDto convertMessageDtoFromModel(MessageDto message){
        return MessageDto.builder()
                .content(message.getContent())
                .id(message.getId())
                .receiverUsername(message.getReceiverUsername())
                .senderUserName(message.getSenderUserName())
                .unread(message.isUnread())
                .sentTime(message.getSentTime())
                .build();
    }

    public static CreateAdDto createNewAdvertisementFromDto(CreateAdvertisementDto createAdvertisementDto){
        return CreateAdDto.builder()
                .creator(createAdvertisementDto.getCreator())
                .category(createAdvertisementDto.getCategory())
                .brand(createAdvertisementDto.getBrand())
                .condition(createAdvertisementDto.getCondition())
                .price(createAdvertisementDto.getPrice())
                .title(createAdvertisementDto.getTitle())
                .type(createAdvertisementDto.getType())
                .build();
    }
    public static UpdateAdvertisementDto createUpdateAdvertisementFromDto(CreateAdvertisementDto createAdvertisementDto){
        return UpdateAdvertisementDto.builder()
            .category(createAdvertisementDto.getCategory())
            .brand(createAdvertisementDto.getBrand())
            .condition(createAdvertisementDto.getCondition())
            .price(createAdvertisementDto.getPrice())
            .title(createAdvertisementDto.getTitle())
            .type(createAdvertisementDto.getType())
            .build();
    }
    public static AdvertisementAllDataDto convertAdvertisementModelToAllDto(AdvertisementDto advertisement){
        return AdvertisementAllDataDto.builder()
                .id(advertisement.getId())
                .brand(advertisement.getBrand())
                .created(advertisement.getCreated())
                .price(advertisement.getPrice())
                .state(advertisement.getState())
                .condition(advertisement.getCondition())
                .title(advertisement.getTitle())
                .type(advertisement.getType())
                .imagePaths(advertisement.getImages().stream().map(Image::getPath).collect(Collectors.toList()))
                .creator(advertisement.getCreator())
                .category(advertisement.getCategory())
                .build();
    }
//    public static Advertisement convertUpdateAdvertisementDtoToModel(UpdateAdvertisementDto advertisementDto){
//        return Advertisement.builder()
//                .id(advertisementDto.getId())
//                .brand(advertisementDto.getBrand())
//                .condition(advertisementDto.getCondition())
//                .price(advertisementDto.getPrice())
//                .state(advertisementDto.getState())
//                .title(advertisementDto.getTitle())
//                .type(advertisementDto.getType())
//                .category(advertisementDto.getCategory())
//                .build();
//    }
    public static AdvertisementDetailsDto convertAdDetailsToDto(AdDetailsDto adDetails){
        return AdvertisementDetailsDto.builder()
                .accelaration(adDetails.getAccelaration())
                .year(adDetails.getYear())
                .batterySize(adDetails.getBatterySize())
                .chargeSpeed(adDetails.getChargeSpeed())
                .performance(adDetails.getPerformance())
                .seatNumber(adDetails.getSeatNumber())
                .km(adDetails.getKm())
                .color(adDetails.getColor())
                .drive(adDetails.getDrive())
                .description(adDetails.getDescription())
                .maxSpeed(adDetails.getMaxSpeed())
                .range(adDetails.getRange())
                .weight(adDetails.getWeight())
                .build();
    }
    public static AdDetailsDto convertAdvertisementDetailsDtoToModel(AdvertisementDetailsDto advertisementDetailsDto){
        return AdDetailsDto.builder()
                .accelaration(advertisementDetailsDto.getAccelaration())
                .year(advertisementDetailsDto.getYear())
                .batterySize(advertisementDetailsDto.getBatterySize())
                .chargeSpeed(advertisementDetailsDto.getChargeSpeed())
                .performance(advertisementDetailsDto.getPerformance())
                .seatNumber(advertisementDetailsDto.getSeatNumber())
                .km(advertisementDetailsDto.getKm())
                .color(advertisementDetailsDto.getColor())
                .drive(advertisementDetailsDto.getDrive())
                .description(advertisementDetailsDto.getDescription())
                .maxSpeed(advertisementDetailsDto.getMaxSpeed())
                .range(advertisementDetailsDto.getRange())
                .weight(advertisementDetailsDto.getWeight())
                .build();
    }
    public static <T> T convertSearchParamsToObject(Map<String,String> searchParams, Class<T> classToConvert){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(searchParams);
        return gson.fromJson(jsonElement, classToConvert);
    }
}
