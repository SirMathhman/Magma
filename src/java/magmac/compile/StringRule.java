package magmac.compile;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<MapNode> apply(String input) {
        return Optional.of(new MapNode().putString(this.key, input));
    }
}
