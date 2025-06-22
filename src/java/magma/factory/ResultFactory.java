package magma.factory;

import magma.error.FormattedError;
import magma.list.ListLike;

public interface ResultFactory<Node, NodeResult, StringResult> {
    NodeResult fromNode(Node node);

    StringResult fromStringError(String message, Node node);

    StringResult fromString(String value);

    NodeResult fromNodeErrorWithChildren(String message, String context, ListLike<FormattedError> errors);

    StringResult fromStringErrorWithChildren(String message, Node context, ListLike<FormattedError> errors);
}