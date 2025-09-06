package az.company.msuser.controller;

import az.company.msuser.model.dto.request.AuthRequest;
import az.company.msuser.model.dto.request.UserRequest;
import az.company.msuser.model.dto.response.UserResponse;
import az.company.msuser.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static az.company.msuser.model.enums.CrudMessages.*;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('SUBMITTER','APPROVER')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return OPERATION_CREATED.getMessage();
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        userService.deleteUser(userId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping
    public ResponseEntity<String> updateUser(Authentication auth, @RequestBody UserRequest userRequest) {
        Long userId = Long.parseLong(auth.getName());
        userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }

    @GetMapping("/name")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/validation")
    public Boolean userValid(@RequestBody AuthRequest authRequest) {
        return userService.userIsValid(authRequest);
    }
}
