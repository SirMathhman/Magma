package magma.app.compile;

public class ParseException extends CompileException {
    public ParseException(String message, String context) {
        super(message + ": " + context);
    }

    public ParseException(String message, String context, CompileException cause) {
        super(message + ": " + context, cause);
    }
}
