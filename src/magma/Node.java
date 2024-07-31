package magma;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Node {
    private final Map<String, String> strings;
    private final Map<String, Node> nodes;
    private final Optional<String> type;

    public Node() {
        this(Optional.empty(), Collections.emptyMap(), Collections.emptyMap());
    }

    public Node(Optional<String> type, Map<String, String> strings, Map<String, Node> nodes) {
        this.type = type;
        this.strings = strings;
        this.nodes = nodes;
    }

    public Node withString(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy, nodes);
    }

    public Optional<String> findString(String propertyKey) {
        return Optional.ofNullable(strings.get(propertyKey));
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings, nodes);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }

    public Node merge(Node other) {
        var stringsCopy = new HashMap<>(strings);
        stringsCopy.putAll(other.strings);

        var nodesCopy = new HashMap<>(nodes);
        nodesCopy.putAll(other.nodes);

        return new Node(type, stringsCopy, nodesCopy);
    }

    public Optional<Node> findNode(String propertyKey) {
        return Optional.ofNullable(nodes.get(propertyKey));
    }

    public Node withNode(String propertyKey, Node propertyValue) {
        var copy = new HashMap<>(nodes);
        copy.put(propertyKey, propertyValue);
        return new Node(type, strings, copy);
    }
}