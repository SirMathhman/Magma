package magma.app.compile.result;

import magma.api.collect.list.ListLike;
import magma.api.collect.list.Lists;
import magma.app.compile.node.Node;

import java.util.function.Function;

public record NodeListOk(ListLike<Node> nodes) implements NodeListResult {
    public NodeListOk() {
        this(Lists.empty());
    }

    @Override
    public NodeListResult addResult(final NodeResult result) {
        return result.map(this.nodes::add)
                .match(NodeListOk::new, NodeListErr::new);
    }

    @Override
    public NodeListResult map(final Function<ListLike<Node>, NodeListResult> mapper) {
        return mapper.apply(this.nodes);
    }

    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return this.nodes.stream()
                .map(mapper)
                .collect(new StringResultJoiner());
    }
}
