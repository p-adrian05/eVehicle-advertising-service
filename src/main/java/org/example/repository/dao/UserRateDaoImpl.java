package org.example.repository.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.util.RateQueryParams;
import org.example.exceptions.*;

import org.example.model.UserRate;
import org.example.repository.RateRepository;
import org.example.repository.UserRateRepository;
import org.example.repository.entity.AdvertisementEntity;
import org.example.repository.entity.RateEntity;
import org.example.repository.entity.UserEntity;
import org.example.repository.entity.UserRateEntity;
import org.example.repository.util.ModelEntityConverter;

import org.example.repository.util.RateState;
import org.example.repository.util.UserRateState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;

import java.util.*;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRateDaoImpl implements UserRateDao{

    private final UserRateRepository userRateRepository;
    private final EntityQuery entityQuery;
    private final RateRepository rateRepository;

    @Override
    @Transactional
    public void createUserRate(@NonNull UserRate userRate) throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException {
        UserEntity ratingUserEntity = entityQuery.queryUserEntity(userRate.getRatingUsername());
        UserEntity ratedUserEntity = entityQuery.queryUserEntity(userRate.getRatedUsername());
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisement(userRate.getAdvertisement().getId());
        if(userRate.getRatedState().equals(UserRateState.SELLER)){
            if(userRateRepository.existsByRatingUser_IdAndAdvertisement_Id(ratingUserEntity.getId(),advertisementEntity.getId())){
                throw new UserRateAlreadyExistsException(String.format("Advertisement rating already exists by rating user  %s, ad id: %s",
                        ratingUserEntity.getUsername(),advertisementEntity.getId()));
            }
            if(advertisementEntity.getCreator().getId()!=ratedUserEntity.getId()){
                throw new UnknownUserException(String.format("Unknown user found for Advertisement: username : %s, ad id: %s",
                        ratedUserEntity.getUsername(),advertisementEntity.getId()));
            }
        }else if (userRate.getRatedState().equals(UserRateState.BUYER)){
            if(advertisementEntity.getCreator().getId()!=ratingUserEntity.getId()){
                throw new UnknownUserException(String.format("Unknown user found for Advertisement: username : %s, ad id: %s",
                        ratingUserEntity.getUsername(),advertisementEntity.getId()));
            }
        }

        RateEntity newRateEntity = rateRepository.save(ModelEntityConverter.createNewRateEntity(userRate));
        log.info("Created Rate: {}",newRateEntity);
        UserRateEntity newUserRateEntity = UserRateEntity.builder()
                .rate(newRateEntity)
                .ratedUser(ratedUserEntity)
                .ratingUser(ratingUserEntity)
                .advertisement(advertisementEntity)
                .state(userRate.getRatedState())
                .activationCode(userRate.getActivationCode())
                .status(userRate.getStatus())
                .build();
        log.info("Created User Rate: {}",newUserRateEntity);
        userRateRepository.save(newUserRateEntity);
    }

    @Override
    @Transactional
    public void deleteUserRate(int id) throws UnknownUserRateException {
        if(!userRateRepository.existsById(id)){
            throw new UnknownUserRateException(String.format("User rate not found by id: %s",id));
        }
        userRateRepository.deleteById(id);
        rateRepository.deleteById(id);
        log.info("Deleted Rate id: {}",id);
    }

    @Override
    @Transactional
    public void updateUserRate(@NonNull UserRate userRate) throws UnknownUserRateException {
        Optional<UserRateEntity> userRateEntity = userRateRepository.findById(userRate.getId());
        Optional<RateEntity> rateEntity = rateRepository.findById(userRate.getId());
        if(userRateEntity.isPresent() &&  rateEntity.isPresent()){
            UserRateEntity userRateEntityToUpdate = userRateEntity.get();
            RateEntity rateEntityToUpdate = rateEntity.get();

            log.info("Old Rate to update : {}",rateEntityToUpdate);
            log.info("Old User Rate to update : {}",userRateEntityToUpdate);

            rateEntityToUpdate.setState(userRate.getRateState());
            rateEntityToUpdate.setDescription(userRate.getDescription());
            if(userRate.getStatus()!=null){
                userRateEntityToUpdate.setStatus(userRate.getStatus());
                userRateEntityToUpdate.setActivationCode(userRate.getActivationCode());
            }
            userRateEntityToUpdate.setRate(rateEntityToUpdate);
            userRateRepository.save(userRateEntityToUpdate);
            log.info("Updated Rate : {}",rateEntity);

            return;
        }

        throw new UnknownUserRateException(String.format("User rate not found by id: %s",userRate.getId()));
    }

    @Override
    public Page<UserRate> getRates(RateQueryParams rateQueryParams, Pageable pageable) {
        return userRateRepository.findByRatedUser_UsernameAndStateOrderByRate(rateQueryParams,pageable)
                 .map(userRateEntity -> {
                     UserRate userRate = ModelEntityConverter.convertUserRateEntityToModel(userRateEntity);
                     userRate.setRatedUsername(rateQueryParams.getRatedUsername());
                     return userRate;
                 });
    }
    @Override
    public UserRate getOpenUserRateByActivationCode(String code) throws UnknownUserRateException {
        Optional<UserRateEntity> userRateEntity = userRateRepository.findUserRateEntityByActivationCode(code);
        if(userRateEntity.isPresent()){
            return ModelEntityConverter.convertUserRateEntityToModel(userRateEntity.get());
        }
        throw new UnknownUserRateException("No opened user rate found by activation code");
    }
    @Override
    public int getRatesCountByUsernameAndRateState(String username, RateState rateState){
        return userRateRepository.countByRatedUser_UsernameAndRate_State(username,rateState);
    }
}
