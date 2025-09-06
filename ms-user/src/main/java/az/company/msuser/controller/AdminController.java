package az.company.msuser.controller;

import az.company.msuser.model.dto.request.UserRequest;
import az.company.msuser.model.dto.response.UserResponse;
import az.company.msuser.service.AdminService;
import az.company.msuser.service.UserService;
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
    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createAdmin(@RequestBody UserRequest userRequest) {
        adminService.createAdmin(userRequest);
        return ResponseEntity.ok(OPERATION_CREATED.getMessage());
    }

    @GetMapping
    public ResponseEntity<UserResponse> getAdmin(Authentication auth) {
        Long adminId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(userService.getUserById(adminId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAdmin(Authentication auth) {
        Long adminId = Long.parseLong(auth.getName());
        userService.deleteUser(adminId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping
    public ResponseEntity<String> updateAdmin(Authentication auth, @RequestBody UserRequest userRequest) {
        Long adminId = Long.parseLong(auth.getName());
        userService.updateUser(adminId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(OPERATION_DELETED.getMessage());
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        adminService.updateUser(userId, userRequest);
        return ResponseEntity.ok(OPERATION_UPDATED.getMessage());
    }
}
