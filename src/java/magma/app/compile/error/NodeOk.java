package magma.app.compile.error;

import java.util.Optional;

public record NodeOk<Node>(Node node) implements NodeResult<Node> {
    @Override
    public Optional<Node> findValue() {
        return Optional.of(this.node);
    }
}
