package org.example.controller.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.example.controller.dto.advertisement.*;
import org.example.controller.dto.message.MessageDto;
import org.example.controller.dto.rate.CreateUserRateDto;
import org.example.controller.dto.rate.RateAdvertisementDto;
import org.example.controller.dto.rate.UserRateBasicDto;
import org.example.controller.dto.rate.UserRateDto;
import org.example.controller.dto.user.UserBasicDto;
import org.example.controller.dto.user.UserDataDto;
import org.example.controller.dto.user.UserRegistrationDto;
import org.example.model.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelDtoConverter {



    public static UserBasicDto convertUserToUserBasicDto(User user){
        return UserBasicDto.builder()
                .username(user.getUsername())
                .created(user.getCreated())
                .enabled(user.isEnabled())
                .profileImagePath(user.getProfileImage().getPath())
                .lastLogin(user.getLastLogin())
                .build();
    }
    public static UserDataDto convertUserDataToUserDataDto(UserData userData){
        return UserDataDto.builder()
                .username(userData.getUsername())
                .city(userData.getCity())
                .fullName(userData.getFullName())
                .publicEmail(userData.getPublicEmail())
                .phoneNumber(userData.getPhoneNumber())
                .build();
    }
    public static UserData convertUserDataDtoToUserData(UserDataDto userDataDto){
         return  UserData.builder()
                    .username(userDataDto.getUsername())
                    .city(userDataDto.getCity())
                    .fullName(userDataDto.getFullName())
                    .publicEmail(userDataDto.getPublicEmail())
                    .phoneNumber(userDataDto.getPhoneNumber())
                    .build();
    }
    public static User convertUserRegistrationDtoToUser(UserRegistrationDto userRegistrationDto){
        return User.builder()
                .username(userRegistrationDto.getUsername())
                .email(userRegistrationDto.getEmail())
                .password(userRegistrationDto.getPassword())
                .build();
    }

    public static List<String> convertBindingErrorsToString(List<ObjectError> errors){
        return errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }

    public static UserRateDto convertUserRateModelToDto(UserRate userRate){
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
    public static UserRateBasicDto convertUserRateModelToBasicDto(UserRate userRate){
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
                .activationCode(userRate.getActivationCode())
                .status(userRate.getStatus())
                .build();
    }
    public static UserRate convertCreateUserRateDtoToModel(CreateUserRateDto createUserRateDto){
        return UserRate.builder()
                .rateState(createUserRateDto.getRateState())
                .advertisement(Advertisement.builder().id(createUserRateDto.getAdId()).build())
                .description(createUserRateDto.getDescription())
                .ratedUsername(createUserRateDto.getRatedUsername())
                .ratingUsername(createUserRateDto.getRatingUsername())
                .build();
    }

    public static MessageDto convertMessageDtoFromModel(Message message){
        return MessageDto.builder()
                .content(message.getContent())
                .id(message.getId())
                .receiverUsername(message.getReceiverUsernames().stream().findFirst().orElse("no_data"))
                .senderUserName(message.getSenderUserName())
                .unread(message.isUnread())
                .sentTime(message.getSentTime())
                .build();
    }
    public static AdvertisementDto convertAdvertisementModelToDto(Advertisement advertisement){
        return AdvertisementDto.builder()
                .id(advertisement.getId())
                .brand(advertisement.getBrand())
                .created(advertisement.getCreated())
                .price(advertisement.getPrice())
                .state(advertisement.getState())
                .condition(advertisement.getCondition())
                .basicAdDetails(convertAdDetailsModelToBasicDetailsDto(advertisement.getBasicAdDetails()))
                .title(advertisement.getTitle())
                .type(advertisement.getType())
                .imagePaths(advertisement.getImages().stream().map(Image::getPath).collect(Collectors.toList()))
                .build();
    }

    public static AdvertisementBasicDetailsDto convertAdDetailsModelToBasicDetailsDto(AdDetails adDetails){
        return AdvertisementBasicDetailsDto.builder()
                .year(adDetails.getYear())
                .batterySize(adDetails.getBatterySize())
                .chargeSpeed(adDetails.getChargeSpeed())
                .performance(adDetails.getPerformance())
                .seatNumber(adDetails.getSeatNumber())
                .km(adDetails.getKm())
                .drive(adDetails.getDrive())
                .build();
    }
    public static Advertisement createNewAdvertisementFromDto(CreateAdvertisementDto createAdvertisementDto){
        return Advertisement.builder()
                .creator(createAdvertisementDto.getCreator())
                .category(createAdvertisementDto.getCategory())
                .brand(createAdvertisementDto.getBrand())
                .description(createAdvertisementDto.getDescription())
                .condition(createAdvertisementDto.getCondition())
                .price(createAdvertisementDto.getPrice())
                .state(createAdvertisementDto.getAdState())
                .title(createAdvertisementDto.getTitle())
                .type(createAdvertisementDto.getType())
                .build();
    }
    public static AdvertisementAllDataDto convertAdvertisementModelToAllDto(Advertisement advertisement){
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
    public static AdvertisementDetailsDto convertAdDetailsToDto(AdDetails adDetails){
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
    public static AdDetails convertAdvertisementDetailsDtoToModel(AdvertisementDetailsDto advertisementDetailsDto){
        return AdDetails.builder()
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
