package magma;

public class GeneratingException extends CompileException {
    public GeneratingException(Throwable cause) {
        super(cause);
    }

    public GeneratingException(String message, Node node) {
        super(message + ": " + node.toString());
    }

    public GeneratingException(String message, Node node, CompileException cause) {
        super(message + ": " + node, cause);
    }
}
