package az.company.msdocument.exception;

public class CustomFeignException extends RuntimeException {
    public CustomFeignException(String message) {
        super(message);
    }
}
