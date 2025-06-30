package magma.node.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.node.EverythingNode;

import java.util.Optional;
import java.util.function.Function;

public record NodeErr<Node>(FormatError error) implements NodeResult<Node> {
    public static NodeResult<EverythingNode> create(final String message, final String input) {
        return new NodeErr<>(new CompileError(message, input));
    }

    @Override
    public Optional<Node> toOptional() {
        return Optional.empty();
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent,
                                 final Function<FormatError, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public NodeResult<Node> map(final Function<Node, Node> mapper) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node> flatMap(final Function<Node, NodeResult<Node>> mapper) {
        return this;
    }
}
