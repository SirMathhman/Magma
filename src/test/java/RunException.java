public class RunException extends Exception {
    public RunException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunException(String message) {
        super(message);
    }
}
