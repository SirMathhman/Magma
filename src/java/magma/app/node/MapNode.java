package magma.app.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    private MapNode(final Map<String, String> strings) {
        this.strings = new HashMap<>(strings);
    }

    public static Node empty() {
        final var strings = Collections.<String, String>emptyMap();
        return new MapNode(strings);
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(final String key) {
        if (this.strings.containsKey(key)) {
            final var found = this.strings.get(key);
            return Optional.of(found);
        }
        else
            return Optional.empty();
    }
}