package magma.app.compile;

public class ParseError implements CompileError {
    private final String message;
    private final String context;

    public ParseError(String message, String context) {
        this.message = message;
        this.context = context;
    }

    @Override
    public String format(int depth) {
        var indent = depth < 2 ? "" : "\t".repeat(depth - 2);
        return message + ": " + context.replace("\n", "\n" + indent);
    }

    @Override
    public String formatWithoutContext() {
        return message;
    }
}
