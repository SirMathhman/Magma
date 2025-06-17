package magma.app.compile;

import magma.api.collect.iter.Iterable;

public interface ResultFactory<Node, Error, NodeResult, StringResult> {
    NodeResult fromStringErr(String message, String input);

    StringResult fromNodeErr(String message, Node node);

    NodeResult fromNode(Node value);

    StringResult fromString(String value);

    NodeResult fromStringErrWithChildren(String message, String input, Iterable<Error> errors);

    StringResult fromNodeErrWithChildren(String message, Node node, Iterable<Error> errors);

    NodeListResult<Node, NodeResult> fromEmptyNodeList();

    StringResult fromEmptyString();
}
