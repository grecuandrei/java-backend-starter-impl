package com.store.application.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link Permission}
 */
@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO implements Serializable {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @NotBlank
    private String name;
}