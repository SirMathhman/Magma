package magma.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    public MapNode() {
        this.strings = new HashMap<>();
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> find(final String key) {
        final var maybeValue = this.strings.get(key);
        return Optional.ofNullable(maybeValue);
    }
}