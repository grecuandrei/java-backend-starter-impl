package com.store.application.role;

import com.store.application.exceptions.PermissionNotFoundException;
import com.store.application.exceptions.RoleAlreadyExistsException;
import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.permission.Permission;
import com.store.application.permission.PermissionRepository;
import com.store.application.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleMapper roleMapper;

    public List<RoleDTO> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> getRoleById(UUID id) {
        log.info("Fetching role with id: {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::toDTO);
    }

    public List<RoleDTO> getRolesForUserId(UUID userId) {
        log.info("Fetching roles for user with id: {}", userId);
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .map(roleMapper::toDTO)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating new role: {}", roleDTO);
        if (roleRepository.findByName(roleDTO.getName()).isPresent()) {
            log.error("Role with name {} already exists", roleDTO.getName());
            throw new RoleAlreadyExistsException("Role with name " + roleDTO.getName() + " already exists");
        }
        Role role = roleMapper.toEntity(roleDTO);
        role.setPermissions(roleDTO.getPermissions().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new PermissionNotFoundException("Permission not found with id: " + permissionId)))
                .collect(Collectors.toSet()));
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Transactional
    public RoleDTO updateRole(RoleDTO updatedRoleDTO) {
        log.info("Updating role with id: {}", updatedRoleDTO.getId());
        return roleRepository.findById(updatedRoleDTO.getId()).map(role -> {
            role.setName(updatedRoleDTO.getName());
            role.setDescription(updatedRoleDTO.getDescription());

            // Validate and set permissions
            Set<Permission> permissions = updatedRoleDTO.getPermissions().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid permission id: " + permissionId)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);

            log.info("Updated role: {}", role);
            return roleMapper.toDTO(roleRepository.save(role));
        }).orElseThrow(() -> {
            log.error("Role not found with id: {}", updatedRoleDTO.getId());
            return new RoleNotFoundException("Role not found with id: " + updatedRoleDTO.getId());
        });
    }

    @Transactional
    public void deleteRole(UUID id) {
        log.info("Deleting role with id: {}", id);
        if (!roleRepository.existsById(id)) {
            log.error("Role not found with id: {}", id);
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}
