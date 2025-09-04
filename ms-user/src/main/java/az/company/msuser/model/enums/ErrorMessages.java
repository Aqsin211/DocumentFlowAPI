package az.company.msuser.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {
    ACTION_REFUSED("ACTION REFUSED"),
    SERVER_ERROR("Unexpected error occurred"),
    USER_EXISTS("User already exists"),
    GMAIL_AT_USE("Gmail is already at use"),
    PHONE_AT_USE("Phone number already at use"),
    USER_DOES_NOT_EXIST("User does not exist");
    private final String message;
}
