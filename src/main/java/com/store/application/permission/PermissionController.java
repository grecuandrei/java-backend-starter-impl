package com.store.application.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@AllArgsConstructor
@Slf4j
public class PermissionController {
    private PermissionService permissionService;

    @Operation(summary = "Fetching all permissions", tags = { "permission", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetched all permissions successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))}
            )
    })
    @PreAuthorize("@securityService.hasPermission('READ')")
    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @Operation(summary = "Fetching permission with id", tags = { "permission", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PreAuthorize("@securityService.hasPermission('READ')")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@Parameter(description = "Permission id to get data for", required = true) @PathVariable UUID id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return new ResponseEntity<>(permission, HttpStatus.OK);
    }

    @Operation(summary = "Creating new permission", tags = { "permission", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new permission successfully")
    })
    @PreAuthorize("@securityService.hasPermission('WRITE')")
    @PostMapping
    public ResponseEntity<PermissionDTO> createPermission(@Parameter(description = "Permission data to create", required = true) @Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @Operation(summary = "Updating permission", tags = { "permission", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PreAuthorize("@securityService.hasPermission('WRITE')")
    @PutMapping
    public ResponseEntity<PermissionDTO> updatePermission(@Parameter(description = "Permission with updated data", required = true) @Valid @RequestBody PermissionDTO updatedPermissionDTO) {
        PermissionDTO permission = permissionService.updatePermission(updatedPermissionDTO);
        return new ResponseEntity<>(permission, HttpStatus.OK);
    }

    @Operation(summary = "Deleting permission with id", tags = { "permission", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted permission successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PreAuthorize("@securityService.hasPermission('WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@Parameter(description = "Permission id to delete", required = true) @PathVariable UUID id) {
        permissionService.deletePermission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
