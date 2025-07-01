package magma.compile.result;

import magma.error.FormatError;
import magma.node.result.NodeListResult;
import magma.node.result.NodeResult;

import java.util.List;

public interface ResultFactory<Node, Error, StringResult> {
    NodeListResult<Node> createNodeList();

    StringResult createStringError(String message, Node node);

    StringResult createStringErrorWithChildren(String message, Node context, List<Error> errors);

    NodeResult<Node, FormatError> createNodeError(String message, String context);

    NodeResult<Node, FormatError> createNodeErrorWithChildren(String message, String context, List<Error> errors);

    NodeResult<Node, FormatError> createNode(Node node);

    StringResult createString(String value);

    StringResult createString();
}
