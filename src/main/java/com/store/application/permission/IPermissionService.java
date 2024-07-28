package com.store.application.permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPermissionService {
    List<Permission> getAllPermissions();
    Optional<Permission> getPermissionById(UUID id);
    Permission createPermission(Permission permission);
    Permission updatePermission(Permission updatedPermission);
    void deletePermission(UUID id);
}
