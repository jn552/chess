package exception;

public class BadRequestException extends Exception{
/**
 * Indicates bad request
 */
    public BadRequestException(String message) {
        super(message);
    }
}