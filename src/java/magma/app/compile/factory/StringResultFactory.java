package magma.app.compile.factory;

public interface StringResultFactory<Node, StringResult> {
    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);
}
