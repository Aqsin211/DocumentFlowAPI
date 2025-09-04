package az.company.msuser.service.abstraction;

import az.company.msuser.model.request.AuthRequest;
import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;

public interface UserService {
    void createUser(UserRequest userRequest);

    UserResponse getUserById(Long userId);

    void deleteUser(Long userId);

    void updateUser(Long userId, UserRequest userRequest);

    Boolean userIsValid(AuthRequest authRequest);

    UserResponse getUserByUsername(String username);
}
