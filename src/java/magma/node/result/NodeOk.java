package magma.node.result;

import magma.error.CompileError;

import java.util.Optional;
import java.util.function.Function;

public record NodeOk<Node>(Node node) implements NodeResult<Node> {
    @Override
    public Optional<Node> toOptional() {
        return Optional.of(this.node);
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent,
                                 final Function<CompileError, Return> whenErr) {
        return whenPresent.apply(this.node);
    }

    @Override
    public NodeResult<Node> map(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }
}
