package az.company.msuser.service.util;

import az.company.msuser.dao.repository.UserRepository;
import az.company.msuser.exception.UserExistsException;
import az.company.msuser.model.enums.ErrorMessages;
import org.springframework.stereotype.Component;

@Component
public class UserConflictChecker {

    private final UserRepository userRepository;

    public UserConflictChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void checkNewUserConflicts(String username, String gmail, String phoneNumber) {
        if (userRepository.existsByUsername(username))
            throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage());

        if (userRepository.existsByGmail(gmail))
            throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage());

        if (userRepository.existsByPhoneNumber(phoneNumber))
            throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage());
    }

    public void checkUpdateConflicts(Long userId, String username, String gmail, String phoneNumber) {
        userRepository.findByUsername(username)
                .filter(u -> !u.getUserId().equals(userId))
                .ifPresent(u -> { throw new UserExistsException(ErrorMessages.USER_EXISTS.getMessage()); });

        userRepository.findByGmail(gmail)
                .filter(u -> !u.getUserId().equals(userId))
                .ifPresent(u -> { throw new UserExistsException(ErrorMessages.GMAIL_AT_USE.getMessage()); });

        userRepository.findByPhoneNumber(phoneNumber)
                .filter(u -> !u.getUserId().equals(userId))
                .ifPresent(u -> { throw new UserExistsException(ErrorMessages.PHONE_AT_USE.getMessage()); });

    }
}
