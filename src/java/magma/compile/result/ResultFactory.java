package magma.compile.result;

import magma.node.result.NodeListResult;
import magma.node.result.NodeResult;

import java.util.List;

public interface ResultFactory<Node, Error, StringResult> {
    NodeListResult<Node, Error> createNodeList();

    StringResult createStringError(String message, Node node);

    StringResult createStringErrorWithChildren(String message, Node context, List<Error> errors);

    NodeResult<Node, Error, StringResult> createNodeError(String message, String context);

    NodeResult<Node, Error, StringResult> createNodeErrorWithChildren(String message,
                                                                      String context,
                                                                      List<Error> errors);

    NodeResult<Node, Error, StringResult> createNode(Node node);

    StringResult createString(String value);

    StringResult createString();
}
