package magma.node.result;

import magma.error.FormatError;

import java.util.function.Function;

public record NodeOk<Node>(Node node) implements NodeResult<Node, FormatError> {

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent,
                                 final Function<FormatError, Return> whenErr) {
        return whenPresent.apply(this.node);
    }

    @Override
    public NodeResult<Node, FormatError> mapValue(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult<Node, FormatError> flatMap(final Function<Node, NodeResult<Node, FormatError>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public NodeResult<Node, FormatError> mapErr(final String message, final String context) {
        return this;
    }
}
