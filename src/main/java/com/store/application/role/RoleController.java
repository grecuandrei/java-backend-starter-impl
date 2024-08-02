package com.store.application.role;

import com.store.application.exceptions.PermissionNotFoundException;
import com.store.application.exceptions.RoleAlreadyExistsException;
import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@Slf4j
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Fetching all roles", tags = { "role", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetched all roles successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))}
            )
    })
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        log.info("Fetching all roles");
        List<RoleDTO> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Fetching role with id", tags = { "role", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched role successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@Parameter(description = "Role id to get data for", required = true) @PathVariable UUID id) {
        log.info("Fetching role with id: {}", id);
        Optional<RoleDTO> role = roleService.getRoleById(id);
        return role.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error("Role not found with id: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @Operation(summary = "Fetching roles for user with id", tags = { "role", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched roles for user successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleDTO>> getRolesForUserId(@Parameter(description = "User id to get roles for", required = true) @PathVariable UUID userId) {
        log.info("Fetching roles for user with id: {}", userId);
        try {
            List<RoleDTO> roles = roleService.getRolesForUserId(userId);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("User not found with id: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Creating new role", tags = { "role", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new role successfully"),
            @ApiResponse(responseCode = "409", description = "Role already exists"),
            @ApiResponse(responseCode = "400", description = "Error creating role")
    })
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Parameter(description = "Role data to create", required = true) @RequestBody RoleDTO roleDTO) {
        log.info("Creating new role: {}", roleDTO.getName());
        try {
            RoleDTO createdRole = roleService.createRole(roleDTO);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch (RoleAlreadyExistsException e) {
            log.error("Error creating role: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (PermissionNotFoundException e) {
            log.error("Permission not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Updating role", tags = { "role", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated role successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping
    public ResponseEntity<RoleDTO> updateRole(@Parameter(description = "Role with updated data", required = true) @RequestBody RoleDTO updatedRoleDTO) {
        log.info("Updating role with id: {}", updatedRoleDTO.getId());
        try {
            RoleDTO role = roleService.updateRole(updatedRoleDTO);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (RoleNotFoundException e) {
            log.error("Role not found with id: {}", updatedRoleDTO.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deleting role with id", tags = { "role", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted role successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@Parameter(description = "Role id to delete data for", required = true) @PathVariable UUID id) {
        log.info("Deleting role with id: {}", id);
        try {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RoleNotFoundException e) {
            log.error("Role not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
