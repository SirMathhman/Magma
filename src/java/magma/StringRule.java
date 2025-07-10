package magma;

import java.util.Optional;

public record StringRule(String key) {
    Optional<String> generate(final Node node) {
        return node.findString(this.key);
    }
}