package com.store.application.role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(UUID id);
    List<Role> getRolesForUserId(UUID userId);
    Role createRole(Role role);
    Role updateRole(Role role);
    void deleteRole(UUID id);
}
