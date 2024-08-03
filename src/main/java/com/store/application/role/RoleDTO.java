package com.store.application.role;

import com.store.application.permission.PermissionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * DTO for {@link Role}
 */
@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO implements Serializable {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @NotNull
    private RoleEnum name;
    @Size(min = 8)
    @NotBlank(message = "Must set a description")
    private String description;
    @NotNull
    private Collection<UUID> permissions;
}