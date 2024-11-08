package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.utils.CustomResponse;
import com.store.application.utils.filters.PageFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private UserService userService;

    @Operation(summary = "Fetching all users", tags = { "users", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully fetched all users",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
            )
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Fetching all users filtered & paginated", tags = { "users", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully fetched all users filtered & paginated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
            )
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CustomResponse<UserDTO>> getAllUsersFilteredAndPaginated(
            @Parameter(description = "Filter & Pageable query", required = true) @Valid @RequestBody PageFilter pageFilter) {
        CustomResponse<UserDTO> users = userService.getAllUsersFilteredAndPaginated(pageFilter);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Fetching user with id", tags = { "users", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "User id to get data for", required = true) @PathVariable UUID id) {
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Parameter(description = "User data to create", required = true) @Valid @RequestBody UserDTO user) {
        try {
            UserDTO createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (RoleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Updating user with id", tags = { "users", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@Parameter(description = "User with updated data", required = true) @Valid @RequestBody UserDTO updatedUser) {
        try {
            UserDTO user = userService.updateUser(updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RoleNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Deleting user with id", tags = { "users", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User id to delete data for", required = true) @PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}