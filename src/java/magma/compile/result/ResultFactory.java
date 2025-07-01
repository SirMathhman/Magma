package magma.compile.result;

import magma.node.result.NodeListResult;

import java.util.List;

public interface ResultFactory<Node, Error, StringResult, NodeResult> {
    NodeListResult<Node, Error> createNodeList();

    StringResult createStringError(String message, Node node);

    StringResult createStringErrorWithChildren(String message, Node context, List<Error> errors);

    NodeResult createNodeError(String message, String context);

    NodeResult createNodeErrorWithChildren(String message, String context, List<Error> errors);

    NodeResult createNode(Node node);

    StringResult createString(String value);

    StringResult createString();
}
