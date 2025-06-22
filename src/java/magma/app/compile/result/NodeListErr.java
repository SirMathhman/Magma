package magma.app.compile.result;

import magma.api.collect.list.ListLike;
import magma.app.compile.node.Node;

import java.util.function.Function;

public record NodeListErr(CompileError error) implements NodeListResult {
    @Override
    public NodeListResult addResult(final NodeResult result) {
        return this;
    }

    @Override
    public NodeListResult map(final Function<ListLike<Node>, NodeListResult> mapper) {
        return this;
    }

    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return new StringErr(this.error);
    }
}
