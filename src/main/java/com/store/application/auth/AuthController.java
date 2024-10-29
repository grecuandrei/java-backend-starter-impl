package com.store.application.auth;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.user.UserDTO;
import com.store.application.user.UserService;
import com.store.application.utils.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/auth")
@RestController
@Slf4j
@AllArgsConstructor
@Tag(name = "Authentication", description = "Auth management APIs")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    // Login endpoint
    @Operation(summary = "Login user", tags = { "auth", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully logged in",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized login")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }

    @Operation(summary = "Register user", tags = { "auth", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully registered",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Role not found")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        log.info("Registering new user: {}", userDTO);
        try {
            String rawPassword = userDTO.getPassword();
            UserDTO createdUser = userService.createUser(userDTO);

            // Authenticate the user after registration
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(createdUser.getUsername(), rawPassword)
            );

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (UserAlreadyExistsException e) {
            log.error("User already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        } catch (RoleNotFoundException e) {
            log.error("Role not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role not found");
        }
    }

    @Operation(summary = "Refresh JWT token", tags = { "auth", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully refreshed token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized refresh")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");
        }
        try {
            assert authHeader != null;
            final String jwt = authHeader.substring(7);
            final String email = jwtTokenUtil.extractEmail(jwt);

            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                    String newToken = jwtTokenUtil.refreshToken(jwt);
                    return ResponseEntity.ok(new AuthResponse(newToken));
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }
}
