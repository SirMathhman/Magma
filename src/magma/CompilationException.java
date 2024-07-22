package magma;

public class CompilationException extends Exception {
    public CompilationException(String message, String context) {
        super(message + ": " + context);
    }
}
