package magma.rule;

import java.util.Optional;

public record NodeOk<Node>(Node node) implements NodeResult<Node> {
    @Override
    public Optional<Node> toOptional() {
        return Optional.of(this.node);
    }
}
