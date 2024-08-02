package com.store.application.role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRoleService {
    List<RoleDTO> getAllRoles();
    Optional<RoleDTO> getRoleById(UUID id);
    List<RoleDTO> getRolesForUserId(UUID userId);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(RoleDTO updatedRoleDTO);
    void deleteRole(UUID id);
}
