package magma.factory;

import magma.error.ErrorList;

public interface ResultFactory<Node, Error, NodeResult, StringResult> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);

    NodeResult fromNodeErrorWithChildren(String message, String context, ErrorList<Error> errors);

    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);

    StringResult fromStringErrorWithChildren(String message, Node context, ErrorList<Error> errors);
}