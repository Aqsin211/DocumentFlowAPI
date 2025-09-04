package az.company.msuser.controller;

import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;
import az.company.msuser.service.concrete.AdminServiceImpl;
import az.company.msuser.service.concrete.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static az.company.msuser.model.enums.CrudMessages.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final AdminServiceImpl adminServiceImpl;
    private final UserServiceImpl userServiceImpl;

    public AdminController(AdminServiceImpl adminServiceImpl, UserServiceImpl userServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public ResponseEntity<String> createAdmin(@RequestBody UserRequest userRequest) {
        adminServiceImpl.createAdmin(userRequest);
        return ResponseEntity.ok(OPERATION_CREATED.getMessage());
    }

    @GetMapping
    public ResponseEntity<UserResponse> getAdmin(Authentication auth) {
        Long adminId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userServiceImpl.getUserById(adminId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAdmin(Authentication auth) {
        Long adminId = Long.parseLong(auth.getName());
        userServiceImpl.deleteUser(adminId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping
    public ResponseEntity<String> updateAdmin(Authentication auth, @RequestBody UserRequest userRequest) {
        Long adminId = Long.parseLong(auth.getName());
        userServiceImpl.updateUser(adminId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminServiceImpl.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminServiceImpl.getUser(userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminServiceImpl.deleteUser(userId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        adminServiceImpl.updateUser(userId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }
}
