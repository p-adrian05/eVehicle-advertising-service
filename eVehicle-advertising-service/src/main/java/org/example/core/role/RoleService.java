package org.example.core.role;

import org.example.core.role.exception.RoleAlreadyExistsException;
import org.example.core.role.exception.RoleModificationException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.user.exception.UnknownUserException;

import java.util.List;

public interface RoleService {

    void createRole(String role) throws RoleAlreadyExistsException;

    void deleteRole(String role) throws UnknownRoleException;

    void addRole(String role, String username) throws UnknownUserException, RoleModificationException;

    void removeRole(String role, String username) throws UnknownUserException, RoleModificationException;

    List<String> getRolesByUsername(String username);

    List<String> readRoles();

}
