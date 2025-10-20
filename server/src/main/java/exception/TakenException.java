package exception;

public class TakenException extends RuntimeException {
    public TakenException(String message) {
        super(message);
    }
}
