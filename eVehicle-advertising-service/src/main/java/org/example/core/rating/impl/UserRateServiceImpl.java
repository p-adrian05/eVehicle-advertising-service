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
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;


    @Override
    @Transactional
    public void createRate(UserRateDto userRate)
        throws UnknownUserException, UnknownAdvertisementException, UserRateAlreadyExistsException,
        UnknownUserRateException {

        UserEntity ratingUserEntity = queryUserEntity(userRate.getRatingUsername());
        UserEntity ratedUserEntity = queryUserEntity(userRate.getRatedUsername());
        AdvertisementEntity advertisementEntity = queryAdEntity(userRate.getAdvertisement().getId());
        String activationCode = null;
        RateStatus rateStatus = RateStatus.OPEN;

        if (userRate.getRatedState().equals(UserRateState.SELLER) &&
            checkRateNotExistsToAdByUsername(userRate.getAdvertisement().getId(), userRate.getRatingUsername())) {
            if (isValidAdCreator(advertisementEntity, userRate.getRatedUsername())) {
                activationCode = UUID.randomUUID().toString();
            }
        }
        if (userRate.getRatedState().equals(UserRateState.BUYER) &&
            isValidAdCreator(advertisementEntity, userRate.getRatingUsername())) {
            activateUserRate(userRate.getActivationCode(), userRate.getRatedUsername(), userRate.getRatingUsername());
            rateStatus = RateStatus.CLOSED;
        }

        RateEntity newRateEntity = createUserRate(userRate.getDescription(), userRate.getRateState());
        log.info("Created Rate: {}", newRateEntity);
        UserRateEntity newUserRateEntity = UserRateEntity.builder()
            .id(UserRateId.builder().rateId(newRateEntity.getId()).ratedUserId(ratedUserEntity.getId())
                .ratingUserId(
                    ratingUserEntity.getId()).build())
            .rate(newRateEntity)
            .ratedUser(ratedUserEntity)
            .ratingUser(ratingUserEntity)
            .advertisement(advertisementEntity)
            .state(userRate.getRatedState())
            .activationCode(activationCode)
            .status(rateStatus)
            .build();
        log.info("Created User Rate: {}", newUserRateEntity);
        userRateRepository.save(newUserRateEntity);
    }

    private boolean isValidAdCreator(AdvertisementEntity advertisementEntity, String creator)
        throws UnknownUserException {
        if (!advertisementEntity.getCreator().getUsername().equals(creator)) {
            throw new UnknownUserException(
                String.format("Unknown user found for Advertisement: username : %s, ad id: %s",
                    creator, advertisementEntity.getId()));
        }
        return true;
    }

    private boolean checkRateNotExistsToAdByUsername(int adId, String username) throws UserRateAlreadyExistsException {
        if (userRateRepository
            .existsByRatingUserUsernameAndAdvertisement_Id(username, adId)) {
            throw new UserRateAlreadyExistsException(
                String.format("Advertisement rating already exists by rating user  %s, ad id: %s",
                    username, adId));
        }
        return true;
    }

    private RateEntity createUserRate(String description, RateState rateState) {
        RateEntity newRateEntity = RateEntity.builder()
            .created(new Timestamp(new Date().getTime()))
            .description(description)
            .state(rateState)
            .build();
        log.info("Created Rate: {}", newRateEntity);
        return newRateEntity;
    }

    private void activateUserRate(String code, String ratedUsername, String ratingUsername)
        throws UnknownUserRateException {
        Optional<UserRateEntity> userRateToActivate =
            userRateRepository.findUserRateEntityByActivationCode(code);
        if (userRateToActivate.isEmpty()) {
            throw new UnknownUserRateException("No starter rate opened was found");
        }
        if (userRateToActivate.get().getRatedUser().getUsername().equals(ratingUsername)
            && userRateToActivate.get().getRatingUser().getUsername().equals(ratedUsername)) {
            userRateToActivate.get().setStatus(RateStatus.CLOSED);
            userRateToActivate.get().setActivationCode(null);
            userRateRepository.save(userRateToActivate.get());
        }else{
            throw new UnknownUserRateException("Invalid rating creation attempt");
        }
    }

    @Override
    public void deleteUserRate(int id) throws UnknownUserRateException {
        if (!userRateRepository.existsById(id)) {
            throw new UnknownUserRateException(String.format("User rate not found by id: %s", id));
        }
        userRateRepository.deleteById(id);
        log.info("Deleted Rate id: {}", id);
    }

    @Override
    public Page<UserRateDto> getRates(RateQueryParams rateQueryParams, Pageable pageable) {
        return userRateRepository.findByRatedUser_UsernameAndStateOrderByRate(rateQueryParams, pageable)
            .map(this::convertUserRateEntityToModel);
    }

    @Override
    public Map<RateState, Integer> getRatesCountByUsernameAndRateState(String username) {
        int positiveCount = userRateRepository
            .countByRatedUser_UsernameAndRate_StateAndStatus(username, RateState.POSITIVE, RateStatus.CLOSED);
        int negativeCount = userRateRepository
            .countByRatedUser_UsernameAndRate_StateAndStatus(username, RateState.NEGATIVE, RateStatus.CLOSED);
        Map<RateState, Integer> values = new HashMap<>();
        values.put(RateState.POSITIVE, positiveCount);
        values.put(RateState.NEGATIVE, negativeCount);
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
    private AdvertisementEntity queryAdEntity(int adId) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity>
            advertisementEntity = advertisementRepository.findById(adId);
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException("Advertisement not found");
        }
        return advertisementEntity.get();
    }

    private UserRateDto convertUserRateEntityToModel(UserRateEntity userRateEntity) {
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
