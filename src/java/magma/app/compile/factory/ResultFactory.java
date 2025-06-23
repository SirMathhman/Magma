package magma.app.compile.factory;

public interface ResultFactory<Node, NodeResult, StringResult, ErrorList> extends ParentStringResultFactory<Node, StringResult, ErrorList>,
        ParentNodeResultFactory<Node, NodeResult, ErrorList> {
}