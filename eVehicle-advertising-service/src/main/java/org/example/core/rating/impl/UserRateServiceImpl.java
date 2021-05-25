package org.example.core.rating.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.rating.UserRateService;

import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.rating.model.UserRateDto;
import org.example.core.rating.persistence.entity.RateEntity;
import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateEntity;
import org.example.core.rating.persistence.entity.UserRateId;
import org.example.core.rating.persistence.entity.UserRateState;
import org.example.core.rating.persistence.repository.RateQueryParams;
import org.example.core.rating.persistence.repository.RateRepository;
import org.example.core.rating.persistence.repository.UserRateRepository;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRateServiceImpl implements UserRateService {

    private final UserRateRepository userRateRepository;
    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;


    @Override
    @Transactional
    public void createSellerRate(UserRateDto userRate)
        throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException{

        UserEntity ratingUserEntity = queryUserEntity(userRate.getRatingUsername());
        UserEntity ratedUserEntity = queryUserEntity(userRate.getRatedUsername());
        Optional<AdvertisementEntity>
            advertisementEntity = advertisementRepository.findById(userRate.getAdvertisement().getId());
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException("Advertisement not found");
        }
        String activationCode = null;
        RateStatus rateStatus = RateStatus.OPEN;

        if (userRate.getRatedState().equals(UserRateState.SELLER)) {
            if (userRateRepository
                .existsByRatingUser_IdAndAdvertisement_Id(ratingUserEntity.getId(),
                    advertisementEntity.get().getId())) {
                throw new UserRateAlreadyExistsException(
                    String.format("Advertisement rating already exists by rating user  %s, ad id: %s",
                        ratingUserEntity.getUsername(), advertisementEntity.get().getId()));
            }
            if (advertisementEntity.get().getCreator().getId() != ratedUserEntity.getId()) {
                throw new UnknownUserException(
                    String.format("Unknown user found for Advertisement: username : %s, ad id: %s",
                        ratedUserEntity.getUsername(), advertisementEntity.get().getId()));
            }
            activationCode = UUID.randomUUID().toString();

            RateEntity newRateEntity = rateRepository.save(RateEntity.builder()
                .created(new Timestamp(new Date().getTime()))
                .description(userRate.getDescription())
                .state(userRate.getRateState())
                .build());
            log.info("Created Rate: {}", newRateEntity);
            UserRateEntity newUserRateEntity = UserRateEntity.builder()
                .id(UserRateId.builder().rateId(newRateEntity.getId()).ratedUserId(ratedUserEntity.getId()).ratingUserId(
                    ratingUserEntity.getId()).build())
                .rate(newRateEntity)
                .ratedUser(ratedUserEntity)
                .ratingUser(ratingUserEntity)
                .advertisement(advertisementEntity.get())
                .state(userRate.getRatedState())
                .activationCode(activationCode)
                .status(rateStatus)
                .build();
            log.info("Created User Rate: {}", newUserRateEntity);
            userRateRepository.save(newUserRateEntity);
        }

    }
    @Override
    @Transactional
    public void createBuyerRate(UserRateDto userRate)
        throws UnknownUserException, UnknownAdvertisementException,
        UnknownUserRateException {

        UserEntity ratingUserEntity = queryUserEntity(userRate.getRatingUsername());
        UserEntity ratedUserEntity = queryUserEntity(userRate.getRatedUsername());
        Optional<AdvertisementEntity>
            advertisementEntity = advertisementRepository.findById(userRate.getAdvertisement().getId());
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException("Advertisement not found");
        }
        RateStatus rateStatus = RateStatus.OPEN;
        if (userRate.getRatedState().equals(UserRateState.BUYER)) {

            if (advertisementEntity.get().getCreator().getId() != ratingUserEntity.getId()) {
                throw new UnknownUserException(
                    String.format("Unknown user found for Advertisement: username : %s, ad id: %s",
                        ratingUserEntity.getUsername(), advertisementEntity.get().getId()));
            }
            Optional<UserRateEntity> userRateToActivate =
                userRateRepository.findUserRateEntityByActivationCode(userRate.getActivationCode());
            System.out.println(userRate.getActivationCode());
            if (userRateToActivate.isEmpty()) {
                throw new UnknownUserRateException(String
                    .format("As a buyer rate, it has no starter rate opened by as buyer: %s",
                        userRate.getRatedUsername()));
            }
            if (userRateToActivate.get().getRatedUser().getUsername().equals(userRate.getRatingUsername())
                && userRateToActivate.get().getRatingUser().getUsername().equals(userRate.getRatedUsername())) {
                rateStatus = RateStatus.CLOSED;
                activateUserRate(userRateToActivate.get());

            } else {
                throw new UnknownUserRateException(String.format("Invalid rating creation attempt"));
            }
        }

        RateEntity newRateEntity = rateRepository.save(RateEntity.builder()
            .created(new Timestamp(new Date().getTime()))
            .description(userRate.getDescription())
            .state(userRate.getRateState())
            .build());
        log.info("Created Rate: {}", newRateEntity);
        UserRateEntity newUserRateEntity = UserRateEntity.builder()
            .id(UserRateId.builder().rateId(newRateEntity.getId()).ratedUserId(ratedUserEntity.getId()).ratingUserId(
                ratingUserEntity.getId()).build())
            .rate(newRateEntity)
            .ratedUser(ratedUserEntity)
            .ratingUser(ratingUserEntity)
            .advertisement(advertisementEntity.get())
            .state(userRate.getRatedState())
            .activationCode(null)
            .status(rateStatus)
            .build();
        log.info("Created User Rate: {}", newUserRateEntity);
        userRateRepository.save(newUserRateEntity);

    }


    private void activateUserRate(UserRateEntity userRate) {
        userRate.setStatus(RateStatus.CLOSED);
        userRate.setActivationCode(null);
        userRateRepository.save(userRate);
    }

    @Override
    public void deleteUserRate(int id) throws UnknownUserRateException {
        if (!userRateRepository.existsById(id)) {
            throw new UnknownUserRateException(String.format("User rate not found by id: %s", id));
        }
        userRateRepository.deleteById(id);
        rateRepository.deleteById(id);
        log.info("Deleted Rate id: {}", id);
    }

    @Override
    public Page<UserRateDto> getRates(RateQueryParams rateQueryParams, Pageable pageable) {
        return userRateRepository.findByRatedUser_UsernameAndStateOrderByRate(rateQueryParams,pageable)
            .map(this::convertUserRateEntityToModel);
    }

    @Override
    public Map<RateState, Integer> getRatesCountByUsernameAndRateState(String username){
        int positiveCount = userRateRepository.countByRatedUser_UsernameAndRate_StateAndStatus(username,RateState.POSITIVE,RateStatus.CLOSED);
        int negativeCount = userRateRepository.countByRatedUser_UsernameAndRate_StateAndStatus(username,RateState.NEGATIVE,RateStatus.CLOSED);
        Map<RateState,Integer> values = new HashMap<>();
        values.put(RateState.POSITIVE,positiveCount);
        values.put(RateState.NEGATIVE,negativeCount);
        return values;
    }

    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        log.info("Queried user : {}", userEntity.get());
        return userEntity.get();
    }
    private UserRateDto convertUserRateEntityToModel(UserRateEntity userRateEntity){
        return UserRateDto.builder()
            .ratingUsername(userRateEntity.getRatingUser().getUsername())
            .ratedUsername(userRateEntity.getRatedUser().getUsername())
            .advertisement(AdvertisementDto.builder()
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
}
