package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import com.store.application.utils.LogMessages;
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
        log.info(LogMessages.FETCH_ALL_PERMISSIONS + "{}");
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PermissionDTO> getPermissionById(UUID id) {
        log.info(LogMessages.FETCH_PERMISSION_BY_ID + "{}", id);
        return permissionRepository.findById(id)
                .map(permissionMapper::toDTO);
    }

    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        log.info(LogMessages.CREATE_NEW_PERMISSION + "{}", permissionDTO.getName());
        Permission permission = permissionMapper.toEntity(permissionDTO);
        return permissionMapper.toDTO(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionDTO updatePermission(PermissionDTO updatedPermissionDTO) {
        log.info(LogMessages.UPDATE_PERMISSION + "{}", updatedPermissionDTO.getId());
        return permissionRepository.findById(updatedPermissionDTO.getId()).map(permission -> {
            permission.setName(updatedPermissionDTO.getName());
            log.info(LogMessages.UPDATED_PERMISSION + "{}", permission);
            return permissionMapper.toDTO(permissionRepository.save(permission));
        }).orElseThrow(() -> {
            log.error(LogMessages.PERMISSION_NOT_FOUND + "{}", updatedPermissionDTO.getId());
            return new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND_MESSAGE + updatedPermissionDTO.getId());
        });
    }

    @Transactional
    public void deletePermission(UUID id) {
        log.info(LogMessages.DELETE_PERMISSION + "{}", id);
        if (!permissionRepository.existsById(id)) {
            log.error(LogMessages.PERMISSION_NOT_FOUND + "{}", id);
            throw new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND_MESSAGE + id);
        }
        permissionRepository.deleteById(id);
    }
}
