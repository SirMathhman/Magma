package magma.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Node {
    private final Map<String, List<Node>> nodeLists;
    private final Properties strings;

    public Node() {
        this.strings = new Properties(this::withStrings);
        this.nodeLists = new HashMap<>();
    }

    public Node(Properties strings, Map<String, List<Node>> nodeLists) {
        this.nodeLists = nodeLists;
        this.strings = strings;
    }

    private Node withStrings(Properties properties) {
        return new Node(this.strings, this.nodeLists);
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

    public Properties strings() {
        return this.strings;
    }
}