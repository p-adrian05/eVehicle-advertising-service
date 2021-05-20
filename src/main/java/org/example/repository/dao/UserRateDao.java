package org.example.repository.dao;

import org.example.repository.util.RateQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UnknownUserRateException;
import org.example.exceptions.UserRateAlreadyExistsException;
import org.example.model.UserRate;
import org.example.repository.util.RateState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRateDao {

    void createUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException;

    void deleteUserRate(int id) throws UnknownUserRateException;

    void updateUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UnknownUserRateException;

     Page<UserRate> getRates(RateQueryParams rateQueryParams, Pageable pageable);

     int getRatesCountByUsernameAndRateState(String username, RateState rateState) throws UnknownUserException;

     UserRate getOpenUserRateByActivationCode(String code) throws UnknownUserRateException;

}
