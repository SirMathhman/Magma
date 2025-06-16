package magma.app.compile.error;

import magma.api.Error;

import java.util.List;

public interface CompileResultFactory<Node, StringResult, NodeResult, NodeListResult> {
    NodeResult fromNode(Node node);

    StringResult fromString(String value);

    StringResult fromNodeError(String message, Node context);

    NodeResult fromStringError(String message, String context);

    NodeListResult fromEmptyNodeList();

    StringResult fromEmptyString();

    NodeResult fromStringErrorWithChildren(String message, String context, List<Error> errors);

    StringResult fromNodeErrorWithChildren(String invalidCombination, Node node, List<Error> errors);
}