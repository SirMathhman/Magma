package magmac.compile;

import java.util.HashMap;
import java.util.Map;

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

    public String findStringOrEmpty(String key) {
        return this.strings.getOrDefault(key, "");
    }

    public MapNode merge(MapNode other) {
        this.strings.putAll(other.strings);
        return this;
    }
}