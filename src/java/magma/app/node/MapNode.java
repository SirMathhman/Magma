package magma.app.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements Node {
    private final Map<String, String> strings;
    private final Map<String, List<Node>> nodeLists;

    public MapNode() {
        this(new HashMap<>(), new HashMap<>());
    }

    public MapNode(Map<String, String> strings, Map<String, List<Node>> nodeLists) {
        this.strings = strings;
        this.nodeLists = nodeLists;
    }

    @Override
    public Node withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(String key) {
        if (this.strings.containsKey(key))
            return Optional.of(this.strings.get(key));
        return Optional.empty();
    }

    @Override
    public Stream<Map.Entry<String, String>> streamStrings() {
        return this.strings.entrySet().stream();
    }

    @Override
    public Node merge(Node other) {
        return other.streamStrings().<Node>reduce(this, (current, entry) -> current.withString(entry.getKey(), entry.getValue()), (_, next) -> next);
    }

    @Override
    public Node withNodeList(String key, List<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public Optional<List<Node>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key))
            return Optional.of(this.nodeLists.get(key));
        else
            return Optional.empty();
    }
}