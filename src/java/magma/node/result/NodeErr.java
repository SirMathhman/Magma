package magma.node.result;

import magma.error.CompileError;
import magma.error.FormatError;

import java.util.List;
import java.util.function.Function;

public record NodeErr<Node>(FormatError error) implements NodeResult<Node, FormatError> {

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent,
                                 final Function<FormatError, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public NodeResult<Node, FormatError> mapValue(final Function<Node, Node> mapper) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, FormatError> flatMap(final Function<Node, NodeResult<Node, FormatError>> mapper) {
        return this;
    }

    @Override
    public NodeResult<Node, FormatError> mapErr(final String message, final String context) {
        return new NodeErr<>(new CompileError(message, context, List.of(this.error)));
    }
}
