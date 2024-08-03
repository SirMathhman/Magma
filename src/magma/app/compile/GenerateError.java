package magma.app.compile;

public class GenerateError implements CompileError {
    private final String message;
    private final Node node;

    public GenerateError(String message, Node node) {
        this.message = message;
        this.node = node;
    }

    @Override
    public String format() {
        return message + ": " + node.format();
    }
}
