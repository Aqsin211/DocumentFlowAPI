package az.company.msauth.exception;

public class CustomFeignException extends RuntimeException {
    public CustomFeignException(String message) {
        super(message);
    }
}
