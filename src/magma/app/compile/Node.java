package magma.app.compile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record Node(Optional<String> type, Map<String, String> strings, Map<String, Node> nodes,
                   Map<String, List<Node>> nodeLists) {
    public Node() {
        this(Optional.empty(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    Optional<String> findString(String key) {
        return Optional.ofNullable(strings().get(key));
    }

    public Node withString(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy, nodes, nodeLists);
    }

    public Node merge(Node other) {
        var stringCopy = new HashMap<>(strings);
        stringCopy.putAll(other.strings);

        var nodesCopy = new HashMap<>(nodes);
        nodesCopy.putAll(other.nodes);

        var nodeListsCopy = new HashMap<>(nodeLists);
        nodeListsCopy.putAll(other.nodeLists);
        return new Node(type, stringCopy, nodesCopy, nodeListsCopy);
    }

    public Node mapString(String propertyKey, Function<String, String> mapper) {
        return findString(propertyKey)
                .map(mapper)
                .map(value -> withString(propertyKey, value))
                .orElse(this);
    }

    public Node withNode(String propertyKey, Node value) {
        var copy = new HashMap<>(nodes);
        copy.put(propertyKey, value);
        return new Node(type, strings, copy, nodeLists);
    }

    public Optional<Node> findNode(String propertyKey) {
        return Optional.ofNullable(nodes.get(propertyKey));
    }

    public boolean has(String propertyKey) {
        return strings.containsKey(propertyKey) || nodes.containsKey(propertyKey);
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings, nodes, nodeLists);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }

    public Node withNodeList(String propertyKey, List<Node> children) {
        var copy = new HashMap<>(nodeLists);
        copy.put(propertyKey, children);
        return new Node(type, strings, nodes, copy);
    }

    public Optional<List<Node>> findNodeList(String propertyKey) {
        return Optional.ofNullable(nodeLists.get(propertyKey));
    }
}