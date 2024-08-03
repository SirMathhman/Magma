package magma.app.compile;

public class ParseError implements CompileError {
    private final String message;
    private final String context;

    public ParseError(String message, String context) {
        this.message = message;
        this.context = context;
    }

    @Override
    public String format() {
        return message + ": " + context;
    }

    @Override
    public String formatWithoutContext() {
        return message;
    }
}
