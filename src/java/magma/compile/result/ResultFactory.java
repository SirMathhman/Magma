package magma.compile.result;

public interface ResultFactory<Node, Error, StringResult, NodeResult>
        extends NodeResultFactory<Node, Error, NodeResult>, StringResultFactory<Node, Error, StringResult> {}
