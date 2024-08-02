package com.store.application.permission;

import com.store.application.exceptions.PermissionNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@Slf4j
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Fetching all permissions", tags = { "permission", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetched all permissions successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))}
            )
    })
    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        log.info("Fetching all permissions");
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @Operation(summary = "Fetching permission with id", tags = { "permission", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@Parameter(description = "Permission id to get data for", required = true) @PathVariable UUID id) {
        log.info("Fetching permission with id: {}", id);
        Optional<PermissionDTO> permission = permissionService.getPermissionById(id);
        return permission.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error("Permission not found with id: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @Operation(summary = "Creating new permission", tags = { "permission", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new permission successfully")
    })
    @PostMapping
    public ResponseEntity<PermissionDTO> createPermission(@Parameter(description = "Permission data to create", required = true) @RequestBody PermissionDTO permissionDTO) {
        log.info("Creating new permission: {}", permissionDTO.getName());
        PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @Operation(summary = "Updating permission", tags = { "permission", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PutMapping
    public ResponseEntity<PermissionDTO> updatePermission(@Parameter(description = "Permission with updated data", required = true) @RequestBody PermissionDTO updatedPermissionDTO) {
        log.info("Updating permission with id: {}", updatedPermissionDTO.getId());
        try {
            PermissionDTO permission = permissionService.updatePermission(updatedPermissionDTO);
            return new ResponseEntity<>(permission, HttpStatus.OK);
        } catch (PermissionNotFoundException e) {
            log.error("Permission not found with id: {}", updatedPermissionDTO.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deleting permission with id", tags = { "permission", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@Parameter(description = "Permission id to delete", required = true) @PathVariable UUID id) {
        log.info("Deleting permission with id: {}", id);
        try {
            permissionService.deletePermission(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PermissionNotFoundException e) {
            log.error("Permission not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
