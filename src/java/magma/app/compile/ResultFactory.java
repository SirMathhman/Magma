package magma.app.compile;

import magma.app.compile.list.NodeListResult;

import java.util.List;

public interface ResultFactory<Node, Error, NodeResult, StringResult> {
    NodeResult fromStringErr(String message, String input);

    StringResult fromNodeErr(String message, Node node);

    NodeResult fromNode(Node value);

    StringResult fromString(String value);

    NodeResult fromStringErrWithChildren(String message, String input, List<Error> errors);

    StringResult fromNodeErrWithChildren(String message, Node node, List<Error> errors);

    NodeListResult<Node, NodeResult> fromEmptyNodeList();

    StringResult fromEmptyString();
}
