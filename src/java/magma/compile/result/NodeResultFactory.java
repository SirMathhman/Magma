package magma.compile.result;

import magma.node.result.NodeListResult;

import java.util.List;

public interface NodeResultFactory<Node, Error, NodeResult> {
    NodeListResult<NodeResult> createNodeList();

    NodeResult createNodeError(String message, String context);

    NodeResult createNodeErrorWithChildren(String message, String context, List<Error> errors);

    NodeResult createNode(Node node);
}
