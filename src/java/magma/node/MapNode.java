package magma.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    public MapNode(final Map<String, String> strings) {this.strings = strings;}

    public MapNode() {
        this(new HashMap<>());
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> find(final String key) {
        return Optional.ofNullable(this.strings.get(key));
    }
}