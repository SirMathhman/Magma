package magma.node.result;

import java.util.Optional;

public record NodeErr<Node>() implements NodeResult<Node> {
    @Override
    public Optional<Node> toOptional() {
        return Optional.empty();
    }
}
