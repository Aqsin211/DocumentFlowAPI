package az.company.msuser.controller;

import az.company.msuser.model.request.AuthRequest;
import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;
import az.company.msuser.service.concrete.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static az.company.msuser.model.enums.CrudMessages.*;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('SUBMITTER','APPROVER')")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public String createUser(@RequestBody UserRequest userRequest) {
        userServiceImpl.createUser(userRequest);
        return OPERATION_CREATED.getMessage();
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userServiceImpl.getUserById(userId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        userServiceImpl.deleteUser(userId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping
    public ResponseEntity<String> updateUser(Authentication auth, @RequestBody UserRequest userRequest) {
        Long userId = Long.parseLong(auth.getName());
        userServiceImpl.updateUser(userId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }

    @GetMapping("/name")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(userServiceImpl.getUserByUsername(username));
    }

    @PostMapping("/validation")
    public Boolean userValid(@RequestBody AuthRequest authRequest) {
        return userServiceImpl.userIsValid(authRequest);
    }
}
