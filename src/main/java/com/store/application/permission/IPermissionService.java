package com.store.application.permission;

import java.util.List;
import java.util.UUID;

public interface IPermissionService {
    List<PermissionDTO> getAllPermissions();
    PermissionDTO getPermissionById(UUID id);
    PermissionDTO createPermission(PermissionDTO permissionDTO);
    PermissionDTO updatePermission(PermissionDTO updatedPermissionDTO);
    void deletePermission(UUID id);
}
