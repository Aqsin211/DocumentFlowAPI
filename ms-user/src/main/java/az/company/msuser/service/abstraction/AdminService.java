package az.company.msuser.service.abstraction;

import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;

import java.util.List;

public interface AdminService {
    void createAdmin(UserRequest userRequest);

    List<UserResponse> getAllUsers();

    void deleteUser(Long userId);

    UserResponse getUser(Long userId);

    void updateUser(Long userId, UserRequest userRequest);
}
