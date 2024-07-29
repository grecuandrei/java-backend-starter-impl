package com.store.application.role;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(UUID id) {
        log.info("Fetching role with id: {}", id);
        return roleRepository.findById(id);
    }

    public List<Role> getRolesForUserId(UUID userId) {
        log.info("Fetching roles for user with id: {}", userId);
        return userRepository.findById(userId)
                .map(user -> new ArrayList<>(user.getRoles()))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public Role createRole(Role role) {
        log.info("Creating new role: {}", role);
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Role updatedRole) {
        log.info("Updating role with id: {}", updatedRole.getId());
        return roleRepository.findById(updatedRole.getId()).map(role -> {
            role.setName(updatedRole.getName());
            role.setDescription(updatedRole.getDescription());
            role.setPermissions(updatedRole.getPermissions());
            log.info("Updated role: {}", role);
            return roleRepository.save(role);
        }).orElseThrow(() -> {
            log.error("Role not found with id: {}", updatedRole.getId());
            return new RoleNotFoundException("Role not found with id: " + updatedRole.getId());
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
