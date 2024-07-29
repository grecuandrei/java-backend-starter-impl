package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(UUID id) {
        log.info("Fetching permission with id: {}", id);
        return permissionRepository.findById(id);
    }

    @Transactional
    public Permission createPermission(Permission permission) {
        log.info("Creating new permission: {}", permission);
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(Permission updatedPermission) {
        log.info("Updating permission with id: {}", updatedPermission.getId());
        return permissionRepository.findById(updatedPermission.getId()).map(permission -> {
            permission.setName(updatedPermission.getName());
            log.info("Updated permission: {}", permission);
            return permissionRepository.save(permission);
        }).orElseThrow(() -> {
            log.error("Permission not found with id: {}", updatedPermission.getId());
            return new PermissionNotFoundException("Permission not found with id: " + updatedPermission.getId());
        });
    }

    @Transactional
    public void deletePermission(UUID id) {
        log.info("Deleting permission with id: {}", id);
        if (!permissionRepository.existsById(id)) {
            log.error("Permission not found with id: {}", id);
            throw new PermissionNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }
}
