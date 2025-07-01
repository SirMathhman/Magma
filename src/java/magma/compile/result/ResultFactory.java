package magma.compile.result;

import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.TypedNode;

import java.util.List;

public interface ResultFactory<StringResult> {
    <Node extends TypedNode<Node>> StringResult create(String message, Node node);

    StringResult createWithChildren(String message, EverythingNode node, List<FormatError> errors);
}
