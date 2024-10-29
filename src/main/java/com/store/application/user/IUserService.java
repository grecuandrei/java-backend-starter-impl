package com.store.application.user;

import com.store.application.utils.CustomResponse;
import com.store.application.utils.filters.PageFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    List<UserDTO> getAllUsers();
    CustomResponse<UserDTO> getAllUsersFilteredAndPaginated(PageFilter pageFilter);
    Optional<UserDTO> getUserById(UUID id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO updatedUserDTO);
    void deleteUser(UUID id);
}
