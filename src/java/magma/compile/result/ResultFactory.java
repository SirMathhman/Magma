package magma.compile.result;

import magma.error.FormatError;
import magma.node.result.NodeResult;

import java.util.List;

public interface ResultFactory<Node, StringResult> {
    StringResult createStringError(String message, Node node);

    StringResult createStringErrorWithChildren(String message, Node context, List<FormatError> errors);

    NodeResult<Node> createNodeError(String message, String context);

    NodeResult<Node> createNodeErrorWithChildren(String message, String context, List<FormatError> errors);
}
