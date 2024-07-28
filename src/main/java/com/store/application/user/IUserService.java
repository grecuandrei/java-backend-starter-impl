package com.store.application.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    List<User> getAllUsers();
    Optional<User> getUserById(UUID id);
    User createUser(User user);
    User updateUser(User updatedUser);
    void deleteUser(UUID id);
}
