package com.store.application.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Must set a role name")
    private RoleEnum name;

    @NotBlank(message = "Must set a description")
    private String description;

    @NotEmpty
    private Collection<UUID> permissions;
}
