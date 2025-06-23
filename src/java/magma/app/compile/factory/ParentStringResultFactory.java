package magma.app.compile.factory;

public interface ParentStringResultFactory<Node, StringResult, ErrorList> extends StringResultFactory<Node, StringResult> {
    StringResult fromStringErrorWithChildren(String message, Node context, ErrorList errors);
}
