package com.store.application.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * DTO for {@link User}
 */
@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @NotBlank(message = "Must set a username")
    private String username;
    @NotBlank(message = "Must set a email")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Must set a password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotNull
    private Collection<UUID> roles;
}