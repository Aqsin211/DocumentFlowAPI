package az.company.msuser.service;

import az.company.msuser.dao.entity.UserEntity;
import az.company.msuser.dao.repository.UserRepository;
import az.company.msuser.exception.ActionDeniedException;
import az.company.msuser.exception.NotFoundException;
import az.company.msuser.model.enums.UserRoles;
import az.company.msuser.model.dto.request.UserRequest;
import az.company.msuser.model.dto.response.UserResponse;
import az.company.msuser.service.util.UserConflictChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static az.company.msuser.model.enums.ErrorMessages.ACTION_REFUSED;
import static az.company.msuser.model.enums.ErrorMessages.USER_DOES_NOT_EXIST;
import static az.company.msuser.model.enums.UserRoles.*;
import static az.company.msuser.model.mapper.UserMapper.USER_MAPPER;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserConflictChecker userConflictChecker;

    public AdminService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserConflictChecker userConflictChecker) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConflictChecker = userConflictChecker;
    }

    public void createAdmin(UserRequest request) {
        userConflictChecker.checkNewUserConflicts(request.getUsername(), request.getGmail(), request.getPhoneNumber());
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setRole(ADMIN.name());
        userRepository.save(USER_MAPPER.mapRequestToEntity(request));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(USER_MAPPER::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        if (!(user.getRole() == SUBMITTER || user.getRole() == APPROVER)) {
            throw new ActionDeniedException(ACTION_REFUSED.getMessage());
        }

        userRepository.delete(user);
    }

    public UserResponse getUser(Long userId) {
        return userRepository.findById(userId)
                .map(USER_MAPPER::mapEntityToResponse)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));
    }

    public void updateUser(Long userId, UserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        if (!(user.getRole() == SUBMITTER || user.getRole() == APPROVER)) {
            throw new ActionDeniedException(ACTION_REFUSED.getMessage());
        }

        userConflictChecker.checkUpdateConflicts(userId, request.getUsername(), request.getGmail(), request.getPhoneNumber());
        updateFields(user, request);
        userRepository.save(user);
    }

    private void updateFields(UserEntity user, UserRequest request) {
        user.setUsername(request.getUsername());
        user.setGmail(request.getGmail());
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(UserRoles.valueOf(request.getRole()));
        }
    }
}
