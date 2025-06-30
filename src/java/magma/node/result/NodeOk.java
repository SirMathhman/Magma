package magma.node.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk<Node>(Node node) implements NodeResult<Node> {
    @Override
    public Optional<Node> toOptional() {
        return Optional.of(this.node);
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Supplier<Return> whenErr) {
        return whenPresent.apply(this.node);
    }
}
