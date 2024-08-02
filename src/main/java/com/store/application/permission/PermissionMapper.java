package com.store.application.permission;

import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public PermissionDTO toDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

    public Permission toEntity(PermissionDTO permissionDTO) {
        return Permission.builder()
                .id(permissionDTO.getId())
                .name(permissionDTO.getName())
                .build();
    }
}
