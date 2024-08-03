package com.store.application.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    Page<UserDTO> getAllUsers(Pageable pageable);
    Optional<UserDTO> getUserById(UUID id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO updatedUserDTO);
    void deleteUser(UUID id);
}
