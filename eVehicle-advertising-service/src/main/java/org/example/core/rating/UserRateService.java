package org.example.core.rating;


import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.rating.model.UserRateDto;
import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.repository.RateQueryParams;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserRateService {

     void createSellerRate(UserRateDto userRate)
        throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException;

     void createBuyerRate(UserRateDto userRate)
        throws UnknownUserException, UnknownAdvertisementException,
        UnknownUserRateException;

    void deleteUserRate(int id) throws UnknownUserRateException;

    Page<UserRateDto> getRates(RateQueryParams rateQueryParams, Pageable pageable);

    Map<RateState,Integer> getRatesCountByUsernameAndRateState(String username)  throws UnknownUserException;

}
