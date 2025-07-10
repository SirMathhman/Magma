package magma.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Map<String, String> strings = new HashMap<>();

    public static Node createMapNode(final String value) {
        return new MapNode().withString("value", value);
    }

    @Override
    public Optional<String> findString(final String key) {
        return Optional.ofNullable(this.strings.get(key));
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }
}