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
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    public List<PermissionDTO> getAllPermissions() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PermissionDTO> getPermissionById(UUID id) {
        log.info("Fetching permission with id: {}", id);
        return permissionRepository.findById(id)
                .map(permissionMapper::toDTO);
    }

    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        log.info("Creating new permission: {}", permissionDTO);
        Permission permission = permissionMapper.toEntity(permissionDTO);
        return permissionMapper.toDTO(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionDTO updatePermission(PermissionDTO updatedPermissionDTO) {
        log.info("Updating permission with id: {}", updatedPermissionDTO.getId());
        return permissionRepository.findById(updatedPermissionDTO.getId()).map(permission -> {
            permission.setName(updatedPermissionDTO.getName());
            log.info("Updated permission: {}", permission);
            return permissionMapper.toDTO(permissionRepository.save(permission));
        }).orElseThrow(() -> {
            log.error("Permission not found with id: {}", updatedPermissionDTO.getId());
            return new PermissionNotFoundException("Permission not found with id: " + updatedPermissionDTO.getId());
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
