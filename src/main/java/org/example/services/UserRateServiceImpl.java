package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.util.RateQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UnknownUserRateException;
import org.example.exceptions.UserRateAlreadyExistsException;
import org.example.model.UserRate;
import org.example.repository.dao.UserRateDao;
import org.example.repository.util.RateState;
import org.example.repository.util.RateStatus;
import org.example.repository.util.UserRateState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRateServiceImpl implements UserRateService{

    private final UserRateDao userRateDao;

    @Override
    public void createUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException, UnknownUserRateException {
        if(userRate.getRatedUsername().equals(userRate.getRatingUsername())){
            throw new IllegalArgumentException("Rated and rating usernames cannot be the same");
        }
        if(userRate.getRatedState().equals(UserRateState.SELLER)){
            userRate.setActivationCode(UUID.randomUUID().toString());
            userRate.setStatus(RateStatus.OPEN);
            userRateDao.createUserRate(userRate);

        }else if(userRate.getRatedState().equals(UserRateState.BUYER)){
            UserRate userRateToActivate = userRateDao.getOpenUserRateByActivationCode(userRate.getActivationCode());

            if(userRateToActivate.getRatedUsername().equals(userRate.getRatingUsername())
                    && userRateToActivate.getRatingUsername().equals(userRate.getRatedUsername())){

                userRate.setActivationCode(null);
                userRate.setStatus(RateStatus.CLOSED);

                userRateDao.createUserRate(userRate);
                activateUserRate(userRateToActivate);
            }else{
                throw new UnknownUserRateException(String.format("As a buyer rate, it has no starter rate opened by as buyer: %s",userRate.getRatedUsername()));
            }
        }
    }
    private void activateUserRate(UserRate userRate) throws UnknownAdvertisementException, UnknownUserException, UnknownUserRateException {
        userRate.setStatus(RateStatus.CLOSED);
        userRate.setActivationCode(null);
        updateUserRate(userRate);
    }
    @Override
    public void deleteUserRate(int id) throws UnknownUserRateException {
        userRateDao.deleteUserRate(id);
    }

    @Override
    public void updateUserRate(UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UnknownUserRateException {
        userRateDao.updateUserRate(userRate);
    }

    @Override
    public Page<UserRate> getRates(RateQueryParams rateQueryParams, Pageable pageable) {
        return userRateDao.getRates(rateQueryParams,pageable);
    }
    @Override
    public Map<RateState,Integer> getRatesCountByUsernameAndRateState(String username) throws UnknownUserException {
        int positiveCount = userRateDao.getRatesCountByUsernameAndRateState(username,RateState.POSITIVE);
        int negativeCount = userRateDao.getRatesCountByUsernameAndRateState(username,RateState.NEGATIVE);
        Map<RateState,Integer> values = new HashMap<>();
        values.put(RateState.POSITIVE,positiveCount);
        values.put(RateState.NEGATIVE,negativeCount);
        return values;
    }
}
