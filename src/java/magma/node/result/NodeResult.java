package magma.node.result;

import magma.node.Node;
import magma.string.StringResult;

import java.util.function.Function;

public interface NodeResult {
    StringResult flatMap(Function<Node, StringResult> mapper);
}
