package magma.app.compile.error;

import java.util.List;

public interface CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult> {
    NodeResult fromNode(Node node);

    StringResult fromString(String value);

    StringResult fromNodeError(String message, Node context);

    NodeResult fromStringError(String message, String context);

    NodeListResult fromEmptyNodeList();

    StringResult fromEmptyString();

    NodeResult fromStringErrorWithChildren(String message, String context, List<Error> errors);

    StringResult fromNodeErrorWithChildren(String invalidCombination, Node node, List<Error> errors);
}