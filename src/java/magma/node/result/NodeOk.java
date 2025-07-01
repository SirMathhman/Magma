package magma.node.result;

import magma.compile.result.ResultFactory;

import java.util.function.Function;

public record NodeOk<Node, Error, StringResult>(Node node) implements NodeResult<Node, Error, StringResult> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Function<Error, Return> whenErr) {
        return whenPresent.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error, StringResult> mapValue(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult<Node, Error, StringResult> flatMap(final Function<Node, NodeResult<Node, Error, StringResult>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error, StringResult> mapErr(final String message,
                                                        final String context,
                                                        final ResultFactory<Node, Error, StringResult, NodeResult<Node, Error, StringResult>> factory) {
        return this;
    }
}
