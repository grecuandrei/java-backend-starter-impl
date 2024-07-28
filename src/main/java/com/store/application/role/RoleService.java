package com.store.application.role;

import com.store.application.exceptions.RoleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Role updatedRole) {
        return roleRepository.findById(updatedRole.getId()).map(role -> {
            role.setName(updatedRole.getName());
            role.setDescription(updatedRole.getDescription());
            role.setPermissions(updatedRole.getPermissions());
            return roleRepository.save(role);
        }).orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + updatedRole.getId()));
    }

    @Transactional
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}
