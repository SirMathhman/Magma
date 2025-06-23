package magma.app.compile.factory;

public interface ResultFactory<Node, NodeResult, StringResult, ErrorList> extends StringResultFactory<Node, StringResult, ErrorList>,
        ParentNodeResultFactory<Node, NodeResult, ErrorList> {
}