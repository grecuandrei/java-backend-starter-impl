package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(UUID id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(Permission updatedPermission) {
        return permissionRepository.findById(updatedPermission.getId()).map(permission -> {
            permission.setName(updatedPermission.getName());
            return permissionRepository.save(permission);
        }).orElseThrow(() -> new PermissionNotFoundException("Permission not found with id: " + updatedPermission.getId()));
    }

    @Transactional
    public void deletePermission(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new PermissionNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }
}
