package com.store.application.permission;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private UUID id;
    private String name;
}
