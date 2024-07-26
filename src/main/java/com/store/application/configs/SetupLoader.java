package com.store.application.configs;

import com.store.application.permission.Permission;
import com.store.application.permission.PermissionRepository;
import com.store.application.role.Role;
import com.store.application.role.RoleEnum;
import com.store.application.role.RoleRepository;
import com.store.application.user.User;
import com.store.application.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SetupLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Permission readPerm = createPrivilegeIfNotFound("READ_PERM");
        Permission writePerm = createPrivilegeIfNotFound("WRITE_PERM");

        List<Permission> adminPermissions = Arrays.asList(readPerm, writePerm);

        Role adminRole = createRoleIfNotFound(RoleEnum.ADMIN, adminPermissions);
        createRoleIfNotFound(RoleEnum.USER, Collections.singletonList(readPerm));

        User user = userRepository.findByUsername("admin");
        if (user == null) {
            user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Collections.singletonList(adminRole))
                    .build();
            userRepository.save(user);
        }

        alreadySetup = true;
    }

    @Transactional
    Permission createPrivilegeIfNotFound(String name) {
        Permission permission = permissionRepository.findByName(name).orElse(null);
        if (permission == null) {
            permission = Permission.builder()
                    .name(name)
                    .build();
            permissionRepository.save(permission);
        }
        return permission;
    }

    @Transactional
    Role createRoleIfNotFound(RoleEnum name, Collection<Permission> permissions) {
        Role role = roleRepository.findByName(name).orElse(null);
        if (role == null) {
            role = Role.builder()
                    .name(name)
                    .description("ROLE_" + name)
                    .permissions(permissions)
                    .build();
            roleRepository.save(role);
        }
        return role;
    }
}
