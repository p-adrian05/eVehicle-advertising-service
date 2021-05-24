package org.example.controller.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.example.controller.dto.advertisement.AdvertisementDetailsDto;
import org.example.controller.dto.advertisement.CreateAdvertisementDto;
import org.example.controller.dto.user.UpdateUserDataDto;
import org.example.controller.dto.user.UserBasicDto;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.user.model.UserDataDto;
import org.example.core.user.model.UserDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;

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

    public static UserDataDto convertUserDataDtoToUserData(UpdateUserDataDto updateUserDataDto){
         return  UserDataDto.builder()
                    .username(updateUserDataDto.getUsername())
                    .city(updateUserDataDto.getCity())
                    .fullName(updateUserDataDto.getFullName())
                    .publicEmail(updateUserDataDto.getPublicEmail())
                    .phoneNumber(updateUserDataDto.getPhoneNumber())
                    .build();
    }

    public static List<String> convertBindingErrorsToString(List<ObjectError> errors){
        return errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
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

    public static AdDetailsDto convertAdvertisementDetailsDtoToModel(AdvertisementDetailsDto advertisementDetailsDto,int id){
        return AdDetailsDto.builder()
                .accelaration(advertisementDetailsDto.getAccelaration())
                .year(advertisementDetailsDto.getYear())
            .adId(id)
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
