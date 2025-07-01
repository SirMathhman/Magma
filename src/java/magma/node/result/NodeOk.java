package magma.node.result;

import magma.compile.result.ResultFactory;
import magma.string.result.StringResult;

import java.util.function.Function;

public record NodeOk<Node, Error>(Node node) implements NodeResult<Node, Error, StringResult<Error>> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Function<Error, Return> whenErr) {
        return whenPresent.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> mapValue(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> flatMap(final Function<Node, NodeResult<Node, Error, StringResult<Error>>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> mapErr(final String message, final String context,
                                                               final ResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error, StringResult<Error>>> factory) {
        return this;
    }
}
