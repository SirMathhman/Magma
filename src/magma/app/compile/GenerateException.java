package magma.app.compile;

public class GenerateException extends CompileException {
    public GenerateException(Throwable cause) {
        super(cause);
    }

    public GenerateException(String message, Node node) {
        super(message + ": " + node.toString());
    }

    public GenerateException(String message, Node node, CompileException cause) {
        super(message + ": " + node, cause);
    }
}
