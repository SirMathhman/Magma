package magma.app.compile.string;

public interface StringResultFactory<Node, StringResult> {
    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);
}
