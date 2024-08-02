package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
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
@RequestMapping("/users")
@Slf4j
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Fetching all users", tags = { "users", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully fetched all users",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
            )
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Fetching user with id", tags = { "users", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "User id to get data for", required = true) @PathVariable UUID id) {
        log.info("Fetching user with id: {}", id);
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Creating new user", tags = { "users", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Error creating user")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Parameter(description = "User data to create", required = true) @RequestBody UserDTO user) {
        log.info("Creating new user: {}", user);
        try {
            UserDTO createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            log.error("Error creating user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (RoleNotFoundException e) {
            log.error("Role not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Updating user with id", tags = { "users", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@Parameter(description = "User with updated data", required = true) @RequestBody UserDTO updatedUser) {
        log.info("Updating user with id: {}", updatedUser.getId());
        try {
            UserDTO user = userService.updateUser(updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("User not found with id: {}", updatedUser.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RoleNotFoundException e) {
            log.error("Role not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UserAlreadyExistsException e) {
            log.error("Username already exists for another user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Deleting user with id", tags = { "users", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User id to delete data for", required = true) @PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            log.error("User not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}