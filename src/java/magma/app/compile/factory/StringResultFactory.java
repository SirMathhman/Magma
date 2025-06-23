package magma.app.compile.factory;

public interface StringResultFactory<Node, StringResult, ErrorList> {
    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);

    StringResult fromStringErrorWithChildren(String message, Node context, ErrorList errors);
}
