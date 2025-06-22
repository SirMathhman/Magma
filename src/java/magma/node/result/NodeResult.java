package magma.node.result;

import magma.node.Node;
import magma.string.StringResult;

import java.util.function.Function;

public interface NodeResult {
    StringResult generate(Function<Node, StringResult> mapper);
}
