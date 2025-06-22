package magma.app.compile.result;

import magma.api.collect.list.ListLike;
import magma.app.compile.node.Node;

import java.util.function.Function;

public interface NodeListResult {
    NodeListResult addResult(NodeResult result);

    NodeListResult map(Function<ListLike<Node>, NodeListResult> mapper);

    StringResult generate(Function<Node, StringResult> mapper);
}
