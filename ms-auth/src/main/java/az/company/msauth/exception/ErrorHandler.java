package az.company.msauth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static az.company.msauth.model.enums.ErrorMessages.SERVER_ERROR;
import static az.company.msauth.model.enums.ErrorMessages.UNAUTHORIZED_ACCESS;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(UnauthorizedException exception) {
        return ErrorResponse.builder()
                .message(UNAUTHORIZED_ACCESS.getMessage())
                .timestamp(LocalDateTime.now())
                .status(UNAUTHORIZED.value())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .messages(errors)
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ErrorResponse.builder()
                .message(SERVER_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .status(INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(CustomFeignException.class)
    @ResponseStatus(BAD_GATEWAY)
    public ErrorResponse handle(CustomFeignException exception) {
        log.error("Unexpected error occurred", exception);
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .status(BAD_GATEWAY.value())
                .build();
    }

}
