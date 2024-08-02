package com.store.application.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(UUID id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO updatedUserDTO);
    void deleteUser(UUID id);
}
