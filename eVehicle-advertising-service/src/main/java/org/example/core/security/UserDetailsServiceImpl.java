package org.example.core.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.role.persistence.entity.RoleEntity;
import org.example.core.user.model.AuthUserDto;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user;
        user = userRepository.findByUsername(username);
        if(user.isPresent()){
            return new UserDetailsImpl(convertUserEntityToDto(user.get()));
        }
        throw new UsernameNotFoundException(String.format("User not found with username %s",username));
    }

    private AuthUserDto convertUserEntityToDto(UserEntity userEntity){
        return AuthUserDto.builder()
            .email(userEntity.getEmail())
            .roles(userEntity.getRoles().stream().map(RoleEntity::getRoleName).collect(Collectors.toList()))
            .password(userEntity.getPassword())
            .username(userEntity.getUsername())
            .enabled(userEntity.isEnabled())
            .build();
    }
}
