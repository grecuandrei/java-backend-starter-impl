package com.store.application.role;

import com.store.application.permission.Permission;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()))
                .build();
    }

    public Role toEntity(RoleDTO roleDTO) {
        return Role.builder()
                .id(roleDTO.getId())
                .name(roleDTO.getName())
                .description(roleDTO.getDescription())
                .build();
    }
}
