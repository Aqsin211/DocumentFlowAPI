package az.company.msuser.service.concrete;

import az.company.msuser.dao.entity.UserEntity;
import az.company.msuser.dao.repository.UserRepository;
import az.company.msuser.exception.NotFoundException;
import az.company.msuser.model.enums.UserRoles;
import az.company.msuser.model.request.AuthRequest;
import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;
import az.company.msuser.service.abstraction.UserService;
import az.company.msuser.service.util.UserConflictChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static az.company.msuser.model.enums.ErrorMessages.USER_DOES_NOT_EXIST;
import static az.company.msuser.model.mapper.UserMapper.USER_MAPPER;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserConflictChecker userConflictChecker;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserConflictChecker userConflictChecker) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConflictChecker = userConflictChecker;
    }

    @Override
    public void createUser(UserRequest request) {
        userConflictChecker.checkNewUserConflicts(request.getUsername(), request.getGmail(), request.getPhoneNumber());
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() == null || request.getRole().isBlank()) {
            request.setRole(UserRoles.SUBMITTER.name());
        }

        userRepository.save(USER_MAPPER.mapRequestToEntity(request));
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(USER_MAPPER::mapEntityToResponse)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));
    }

    @Override
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        if (!(user.getRole() == UserRoles.SUBMITTER || user.getRole() == UserRoles.APPROVER)) {
            throw new NotFoundException(USER_DOES_NOT_EXIST.getMessage());
        }

        userRepository.delete(user);
    }

    @Override
    public void updateUser(Long userId, UserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        userConflictChecker.checkUpdateConflicts(userId, request.getUsername(), request.getGmail(), request.getPhoneNumber());
        updateFields(user, request);
        userRepository.save(user);
    }

    @Override
    public Boolean userIsValid(AuthRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_DOES_NOT_EXIST.getMessage()));

        return USER_MAPPER.mapEntityToResponse(user);
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
