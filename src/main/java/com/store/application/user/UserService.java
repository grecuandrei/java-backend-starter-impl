package com.store.application.user;

import com.store.application.exceptions.RoleNotFoundException;
import com.store.application.exceptions.UserAlreadyExistsException;
import com.store.application.exceptions.UserNotFoundException;
import com.store.application.role.Role;
import com.store.application.role.RoleRepository;
import com.store.application.utils.CustomResponse;
import com.store.application.utils.LogMessages;
import com.store.application.utils.filters.ObjectSpecification;
import com.store.application.utils.filters.PageFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "users", unless = "#result == null")
    public CustomResponse<UserDTO> getAllUsersFilteredAndPaginated(PageFilter pageFilter) {
        ObjectSpecification<User> specification = new ObjectSpecification<>(pageFilter.getFilters());
        Pageable pageable = PageRequest.of(pageFilter.getPage(), pageFilter.getSize(), Sort.Direction.fromString(pageFilter.getOrder()), pageFilter.getSort());
        Page<UserDTO> usersPage = userRepository.findAll(specification, pageable).map(userMapper::toDTO);

        List<UserDTO> userDTOList = usersPage.getContent();

        return CustomResponse.<UserDTO>builder()
                .content(userDTOList)
                .page(usersPage.getNumber() + 1)
                .size(usersPage.getSize())
                .total(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .last(usersPage.isLast())
                .build();
    }

    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new UserAlreadyExistsException(String.format(LogMessages.USERNAME_ALREADY_EXISTS, userDTO.getUsername()));
        }
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = userMapper.toEntity(userDTO);
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(userDTO.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND + roleId)))
                .collect(Collectors.toSet()));
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserDTO updateUser(UserDTO updatedUserDTO) {
        return userRepository.findById(updatedUserDTO.getId()).map(user -> {
            User userWithSameUsername = userRepository.findByEmail(updatedUserDTO.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(LogMessages.USER_NOT_FOUND_BY_EMAIL + updatedUserDTO.getUsername()));
            if (!userWithSameUsername.getId().equals(updatedUserDTO.getId())) {
                throw new UserAlreadyExistsException(String.format(LogMessages.USERNAME_ALREADY_EXISTS, updatedUserDTO.getUsername()));
            }

            user.setUsername(updatedUserDTO.getUsername());
            user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
            Set<Role> roles = updatedUserDTO.getRoles().stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(LogMessages.ROLE_NOT_FOUND + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            return userMapper.toDTO(userRepository.save(user));
        }).orElseThrow(() -> new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + updatedUserDTO.getId()));
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            log.error(LogMessages.USER_NOT_FOUND_BY_ID + "{}", id);
            throw new UserNotFoundException(LogMessages.USER_NOT_FOUND_BY_ID + id);
        }
        userRepository.deleteById(id);
    }
}
