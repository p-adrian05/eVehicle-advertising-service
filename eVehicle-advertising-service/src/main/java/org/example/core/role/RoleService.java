package org.example.core.role;

import org.example.core.role.exception.RoleAlreadyExistsException;
import org.example.core.role.exception.RoleModificationException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.role.model.Role;
import org.example.core.role.persistence.entity.RoleEntity;
import org.example.core.user.exception.UnknownUserException;

import java.util.List;

public interface RoleService {

    void addRole(String username,Role role) throws UnknownUserException, RoleModificationException;

    void removeRole(String username,Role role) throws UnknownUserException, RoleModificationException;

    List<Role> getRolesByUsername(String username);

    List<Role> readRoles();

    RoleEntity queryDefaultRole();
}
