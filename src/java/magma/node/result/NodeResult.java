package magma.node.result;

import java.util.function.Function;

public sealed interface NodeResult<Node> extends Matching<Node> permits NodeOk, NodeErr {
    NodeResult<Node> map(Function<Node, Node> mapper);
}
