package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.role.Role;
import com.store.application.role.RoleRepository;
import com.store.application.utils.LogMessages;
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
        log.info(LogMessages.FETCHING_ALL_USERS);
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        log.info(LogMessages.FETCHING_USER_BY_ID + "{}", id);
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.info(LogMessages.CREATING_NEW_USER + "{}", userDTO);
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            log.error(String.format(LogMessages.USERNAME_ALREADY_EXISTS, userDTO.getUsername()));
            throw new UserAlreadyExistsException(String.format(LogMessages.USERNAME_ALREADY_EXISTS, userDTO.getUsername()));
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        user.setRoles(userDTO.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND + roleId)))
                .collect(Collectors.toList()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(UserDTO updatedUserDTO) {
        log.info(LogMessages.UPDATING_USER + "{}", updatedUserDTO.getId());
        return userRepository.findById(updatedUserDTO.getId()).map(user -> {
            Optional<User> userWithSameUsername = userRepository.findByUsername(updatedUserDTO.getUsername());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(updatedUserDTO.getId())) {
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
    public void deleteUser(UUID id) {
        log.info(LogMessages.DELETING_USER + "{}", id);
        if (!userRepository.existsById(id)) {
            log.error(LogMessages.USER_NOT_FOUND_BY_ID + "{}", id);
            throw new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + id);
        }
        userRepository.deleteById(id);
    }
}
