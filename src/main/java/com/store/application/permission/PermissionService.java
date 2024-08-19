package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import com.store.application.utils.LogMessages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class PermissionService implements IPermissionService {

    private PermissionRepository permissionRepository;

    private PermissionMapper permissionMapper;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PermissionDTO getPermissionById(UUID id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDTO)
                .orElseThrow(() -> new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND + id));
    }

    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        Permission permission = permissionMapper.toEntity(permissionDTO);
        return permissionMapper.toDTO(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionDTO updatePermission(PermissionDTO updatedPermissionDTO) {
        return permissionRepository.findById(updatedPermissionDTO.getId()).map(permission -> {
            permission.setName(updatedPermissionDTO.getName());
            return permissionMapper.toDTO(permissionRepository.save(permission));
        }).orElseThrow(() -> new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND_MESSAGE + updatedPermissionDTO.getId()));
    }

    @Transactional
    public void deletePermission(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND_MESSAGE + id);
        }
        permissionRepository.deleteById(id);
    }
}
