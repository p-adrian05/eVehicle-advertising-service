package org.example.core.role.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.role.RoleService;
import org.example.core.role.exception.RoleAlreadyExistsException;
import org.example.core.role.exception.RoleModificationException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.role.model.Role;
import org.example.core.role.persistence.entity.RoleEntity;
import org.example.core.role.persistence.repository.RoleRepository;
import org.example.core.user.UserCreateObserver;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoleServiceImpl implements RoleService, UserCreateObserver {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void createRole(Role role) throws RoleAlreadyExistsException {
        if(roleRepository.existsByRoleName(role)){
            throw new RoleAlreadyExistsException(String.format("Role already exists for creating %s",role));
        }
        roleRepository.save(RoleEntity.builder().roleName(role).build());
    }

    @Override
    public void deleteRole(Role role) throws UnknownRoleException {
        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityByRoleName(role);
        if(roleEntity.isEmpty()){
            throw new UnknownRoleException(String.format("Role not exists for deleting %s",role));
        }
        roleRepository.delete(roleEntity.get());
    }

    @Override
    @Transactional
    public void addRole(String username, Role role) throws RoleModificationException {
        UserEntity userEntity = queryUserEntity(username);
        log.info("Adding role to user with username: {}",username);
        modifyRole(role,userEntity::addRole);
        userRepository.save(userEntity);
    }
    @Override
    @Transactional
    public void removeRole(String username, Role role) throws RoleModificationException {
        UserEntity userEntity = queryUserEntity(username);
        log.info("Removing role from user with username: {}",username);
        modifyRole(role,userEntity::removeRole);
        userRepository.save(userEntity);
    }

    @Override
    public List<Role> getRolesByUsername(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        return userEntity
            .map(entity -> entity.getRoles().stream().map(RoleEntity::getRoleName).collect(Collectors.toList()))
            .orElseGet(LinkedList::new);
    }

    @Override
    public List<Role> readRoles(){
       return roleRepository.findAll().stream().map(RoleEntity::getRoleName).collect(Collectors.toList());
    }
    private void modifyRole(Role role, Consumer<RoleEntity> roleModifierConsumer)
        throws RoleModificationException {
        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityByRoleName(role);
        if(roleEntity.isEmpty()){
            throw new RoleModificationException(String.format("Role not found %s",role));
        }
       roleModifierConsumer.accept(roleEntity.get());
    }
    private UserEntity queryUserEntity(String username) throws RoleModificationException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if(userEntity.isEmpty()){
            throw new RoleModificationException(String.format("User not found %s",username));
        }
        return userEntity.get();
    }

    @Override
    public void handleNewUser(UserEntity userEntity) {
        userEntity.setRoles(Set.of(queryDefaultRole()));
    }

    private RoleEntity queryDefaultRole(){
        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityByRoleName(Role.defaultRole());
        if(roleEntity.isEmpty()){
            return roleRepository.save(RoleEntity.builder().roleName(Role.defaultRole()).build());
        }
        return roleEntity.get();
    }
}
