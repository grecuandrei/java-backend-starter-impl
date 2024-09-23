package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.role.Role;
import com.store.application.role.RoleRepository;
import com.store.application.utils.LogMessages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService implements IUserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @Cacheable(cacheNames = "users", unless = "#result == null")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info(LogMessages.FETCHING_ALL_USERS);
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public Optional<UserDTO> getUserById(UUID id) {
        log.info(LogMessages.FETCHING_USER_BY_ID + "{}", id);
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        log.info(LogMessages.CREATING_NEW_USER + "{}", userDTO);
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            log.error(String.format(LogMessages.USERNAME_ALREADY_EXISTS, userDTO.getUsername()));
            throw new UserAlreadyExistsException(String.format(LogMessages.USERNAME_ALREADY_EXISTS, userDTO.getUsername()));
        }
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = userMapper.toEntity(userDTO);
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(userDTO.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND + roleId)))
                .collect(Collectors.toList()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserDTO updateUser(UserDTO updatedUserDTO) {
        log.info(LogMessages.UPDATING_USER + "{}", updatedUserDTO.getId());
        return userRepository.findById(updatedUserDTO.getId()).map(user -> {
            User userWithSameUsername = userRepository.findByUsername(updatedUserDTO.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getId().equals(updatedUserDTO.getId())) {
                log.error(String.format(LogMessages.USERNAME_ALREADY_EXISTS, updatedUserDTO.getUsername()));
                throw new UserAlreadyExistsException(String.format(LogMessages.USERNAME_ALREADY_EXISTS, updatedUserDTO.getUsername()));
            }

            user.setUsername(updatedUserDTO.getUsername());
            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
            Collection<Role> roles = updatedUserDTO.getRoles().stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND + roleId)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
            log.info(LogMessages.UPDATED_USER + "{}", user);
            return userMapper.toDTO(userRepository.save(user));
        }).orElseThrow(() -> {
            log.error(LogMessages.USER_NOT_FOUND_BY_ID + "{}", updatedUserDTO.getId());
            return new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + updatedUserDTO.getId());
        });
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteUser(UUID id) {
        log.info(LogMessages.DELETING_USER + "{}", id);
        if (!userRepository.existsById(id)) {
            log.error(LogMessages.USER_NOT_FOUND_BY_ID + "{}", id);
            throw new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + id);
        }
        userRepository.deleteById(id);
    }
}
