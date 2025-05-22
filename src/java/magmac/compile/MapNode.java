package magmac.compile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MapNode {
    private final Map<String, String> strings;
    private final Map<String, List<MapNode>> nodeLists;

    public MapNode(Map<String, String> strings, Map<String, List<MapNode>> nodeLists) {
        this.strings = strings;
        this.nodeLists = nodeLists;
    }

    public MapNode() {
        this(new HashMap<>(), new HashMap<>());
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

    public MapNode putNodeList(String key, List<MapNode> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    public Optional<List<MapNode>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key)) {
            return Optional.of(this.nodeLists.get(key));
        }
        else {
            return Optional.empty();
        }
    }
}