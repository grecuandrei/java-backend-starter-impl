package com.store.application.permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPermissionService {
    List<PermissionDTO> getAllPermissions();
    Optional<PermissionDTO> getPermissionById(UUID id);
    PermissionDTO createPermission(PermissionDTO permissionDTO);
    PermissionDTO updatePermission(PermissionDTO updatedPermissionDTO);
    void deletePermission(UUID id);
}
