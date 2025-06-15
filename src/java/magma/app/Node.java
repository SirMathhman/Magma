package magma.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Node {
    private final Map<String, String> strings;
    private final Map<String, List<Node>> nodeLists;

    public Node() {
        this(new HashMap<>(), new HashMap<>());
    }

    public Node(Map<String, String> strings, Map<String, List<Node>> nodeLists) {
        this.strings = strings;
        this.nodeLists = nodeLists;
    }

    public Node withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    public Optional<String> findString(String key) {
        if (this.strings.containsKey(key))
            return Optional.of(this.strings.get(key));

        return Optional.empty();
    }

    public Node withNodeList(String key, List<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    public Optional<List<Node>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key))
            return Optional.of(this.nodeLists.get(key));
        return Optional.empty();
    }
}