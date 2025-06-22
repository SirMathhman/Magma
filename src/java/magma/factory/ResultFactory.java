package magma.factory;

import magma.list.ListLike;

public interface ResultFactory<Node, Error, NodeResult, StringResult> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);

    NodeResult fromNodeErrorWithChildren(String message, String context, ListLike<Error> errors);

    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);

    StringResult fromStringErrorWithChildren(String message, Node context, ListLike<Error> errors);
}