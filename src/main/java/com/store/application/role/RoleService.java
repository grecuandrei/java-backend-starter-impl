package com.store.application.role;

import com.store.application.exceptions.PermissionNotFoundException;
import com.store.application.exceptions.RoleAlreadyExistsException;
import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.permission.Permission;
import com.store.application.permission.PermissionRepository;
import com.store.application.user.UserRepository;
import com.store.application.utils.LogMessages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RoleService implements IRoleService {

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private PermissionRepository permissionRepository;

    private RoleMapper roleMapper;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> getRoleById(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO);
    }

    public List<RoleDTO> getRolesForUserId(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .map(roleMapper::toDTO)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + userId));
    }

    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.findByName(roleDTO.getName()) != null) {
            throw new RoleAlreadyExistsException(LogMessages.ROLE_ALREADY_EXISTS_MESSAGE + roleDTO.getName());
        }
        Role role = roleMapper.toEntity(roleDTO);
        role.setPermissions(roleDTO.getPermissions().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new PermissionNotFoundException(LogMessages.PERMISSION_NOT_FOUND_MESSAGE + permissionId)))
                .collect(Collectors.toSet()));
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Transactional
    public RoleDTO updateRole(RoleDTO updatedRoleDTO) {
        return roleRepository.findById(updatedRoleDTO.getId()).map(role -> {
            role.setName(updatedRoleDTO.getName());
            role.setDescription(updatedRoleDTO.getDescription());

            // Validate and set permissions
            Set<Permission> permissions = updatedRoleDTO.getPermissions().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new IllegalArgumentException(LogMessages.INVALID_PERMISSION_ID + permissionId)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);

            return roleMapper.toDTO(roleRepository.save(role));
        }).orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND_MESSAGE + updatedRoleDTO.getId()));
    }

    @Transactional
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND_MESSAGE + id);
        }
        roleRepository.deleteById(id);
    }
}
