package magma.app.compile;

public class NodeException extends CompileException {
    public NodeException(String message, Node node) {
        super(message, node.toString());
    }
}
