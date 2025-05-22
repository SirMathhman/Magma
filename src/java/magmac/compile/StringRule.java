package magmac.compile;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<String> generate(MapNode node) {
        return node.findString(this.key());
    }

    @Override
    public Optional<MapNode> parse(String input) {
        return Optional.of(new MapNode().putString(this.key, input));
    }
}
