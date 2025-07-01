package magma.node.result;

import magma.compile.result.ResultFactory;

import java.util.List;
import java.util.function.Function;

public record NodeErr<Node, Error, StringResult>(Error error) implements NodeResult<Node, Error, StringResult> {

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Function<Error, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public NodeResult<Node, Error, StringResult> mapValue(final Function<Node, Node> mapper) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error, StringResult> flatMap(final Function<Node, NodeResult<Node, Error, StringResult>> mapper) {
        return this;
    }

    @Override
    public NodeResult<Node, Error, StringResult> mapErr(final String message,
                                                        final String context,
                                                        final ResultFactory<Node, Error, StringResult, NodeResult<Node, Error, StringResult>> factory) {
        return factory.createNodeErrorWithChildren(message, context, List.of(this.error));
    }
}
