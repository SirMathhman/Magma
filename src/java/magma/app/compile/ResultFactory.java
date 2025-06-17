package magma.app.compile;

import magma.api.list.Streamable;

public interface ResultFactory<Node, Error, NodeResult, StringResult> {
    NodeResult fromStringErr(String message, String input);

    StringResult fromNodeErr(String message, Node node);

    NodeResult fromNode(Node value);

    StringResult fromString(String value);

    NodeResult fromStringErrWithChildren(String message, String input, Streamable<Error> errors);

    StringResult fromNodeErrWithChildren(String message, Node node, Streamable<Error> errors);

    NodeListResult<Node, NodeResult> fromEmptyNodeList();

    StringResult fromEmptyString();
}
