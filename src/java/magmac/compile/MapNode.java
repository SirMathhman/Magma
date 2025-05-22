package magmac.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode {
    private final Map<String, String> strings;

    public MapNode(Map<String, String> strings) {
        this.strings = strings;
    }

    public MapNode() {
        this(new HashMap<>());
    }

    public MapNode putString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    public Optional<String> findString(String key) {
        if (this.strings.containsKey(key)) {
            return Optional.of(this.strings.get(key));
        }

        return Optional.empty();
    }

    public MapNode merge(MapNode other) {
        this.strings.putAll(other.strings);
        return this;
    }
}