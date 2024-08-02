package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.role.Role;
import com.store.application.role.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO);
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            log.error("User with username {} already exists", userDTO.getUsername());
            throw new UserAlreadyExistsException("User with username " + userDTO.getUsername() + " already exists");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        user.setRoles(userDTO.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId)))
                .collect(Collectors.toList()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(UserDTO updatedUserDTO) {
        log.info("Updating user with id: {}", updatedUserDTO.getId());
        return userRepository.findById(updatedUserDTO.getId()).map(user -> {
            Optional<User> userWithSameUsername = userRepository.findByUsername(updatedUserDTO.getUsername());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(updatedUserDTO.getId())) {
                log.error("Username {} already exists for another user", updatedUserDTO.getUsername());
                throw new UserAlreadyExistsException("Username " + updatedUserDTO.getUsername() + " already exists for another user");
            }

            user.setUsername(updatedUserDTO.getUsername());
            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
            Collection<Role> roles = updatedUserDTO.getRoles().stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
            log.info("Updated user: {}", user);
            return userMapper.toDTO(userRepository.save(user));
        }).orElseThrow(() -> {
            log.error("User not found with id: {}", updatedUserDTO.getId());
            return new UserNotFoundException("User not found with id: " + updatedUserDTO.getId());
        });
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
