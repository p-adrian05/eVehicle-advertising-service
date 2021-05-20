package org.example.services;

import org.example.repository.util.RateQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UnknownUserRateException;
import org.example.exceptions.UserRateAlreadyExistsException;
import org.example.model.UserRate;
import org.example.repository.util.RateState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserRateService {

    void createUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException, UnknownUserRateException;

    void deleteUserRate(int id) throws UnknownUserRateException;

    void updateUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UnknownUserRateException;

    Page<UserRate> getRates(RateQueryParams rateQueryParams, Pageable pageable);

    Map<RateState,Integer> getRatesCountByUsernameAndRateState(String username)  throws UnknownUserException;

}
