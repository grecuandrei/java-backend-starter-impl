package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@Slf4j
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        log.info("Fetching all permissions");
        List<Permission> permissions = permissionService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable UUID id) {
        log.info("Fetching permission with id: {}", id);
        Optional<Permission> permission = permissionService.getPermissionById(id);
        return permission.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error("Permission not found with id: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        log.info("Creating new permission: {}", permission.getName());
        Permission createdPermission = permissionService.createPermission(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission updatedPermission) {
        log.info("Updating permission with id: {}", updatedPermission.getId());
        try {
            Permission permission = permissionService.updatePermission(updatedPermission);
            return new ResponseEntity<>(permission, HttpStatus.OK);
        } catch (PermissionNotFoundException e) {
            log.error("Permission not found with id: {}", updatedPermission.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        log.info("Deleting permission with id: {}", id);
        try {
            permissionService.deletePermission(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PermissionNotFoundException e) {
            log.error("Permission not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
