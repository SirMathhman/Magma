package magma.node.result;

import magma.error.CompileError;

import java.util.Optional;
import java.util.function.Function;

public record NodeErr<Node>(CompileError error) implements NodeResult<Node> {
    @Override
    public Optional<Node> toOptional() {
        return Optional.empty();
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent,
                                 final Function<CompileError, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
