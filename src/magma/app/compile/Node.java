package magma.app.compile;

import magma.api.XMLNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Node {
    private final Map<String, String> strings;
    private final Map<String, Node> nodes;
    private final Optional<String> type;
    private final Map<String, List<Node>> nodeLists;

    public Node() {
        this(Optional.empty(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    public Node(Optional<String> type, Map<String, String> strings, Map<String, Node> nodes, Map<String, List<Node>> nodeLists) {
        this.type = type;
        this.strings = strings;
        this.nodes = nodes;
        this.nodeLists = nodeLists;
    }

    @Override
    public String toString() {
        return "Node{" +
               "strings=" + strings +
               ", nodes=" + nodes +
               ", type=" + type +
               '}';
    }

    public Node withString(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy, nodes, nodeLists);
    }

    public Optional<String> findString(String propertyKey) {
        return Optional.ofNullable(strings.get(propertyKey));
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings, nodes, nodeLists);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }

    public Node merge(Node other) {
        var stringsCopy = new HashMap<>(strings);
        stringsCopy.putAll(other.strings);

        var nodesCopy = new HashMap<>(nodes);
        nodesCopy.putAll(other.nodes);

        return new Node(type, stringsCopy, nodesCopy, nodeLists);
    }

    public Optional<Node> findNode(String propertyKey) {
        return Optional.ofNullable(nodes.get(propertyKey));
    }

    public Node withNode(String propertyKey, Node propertyValue) {
        var copy = new HashMap<>(nodes);
        copy.put(propertyKey, propertyValue);
        return new Node(type, strings, copy, nodeLists);
    }

    public Node mapString(String propertyKey, Function<String, String> mapper) {
        return findString(propertyKey)
                .map(mapper)
                .map(value -> withString(propertyKey, value))
                .orElse(this);
    }

    public Node withNodeList(String propertyKey, List<Node> propertyValues) {
        var copy = new HashMap<>(nodeLists);
        copy.put(propertyKey, propertyValues);
        return new Node(type, strings, nodes, copy);
    }

    public Optional<List<Node>> findNodeList(String propertyKey) {
        return Optional.ofNullable(nodeLists.get(propertyKey));
    }

    public String format() {
        return toString();
    }

    public XMLNode toXML() {
        var node = new XMLNode(type.orElse("?"));
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            node = node.withAttribute(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            var child = entry.getValue().toXML().withAttribute("for", entry.getKey());
            node = node.withChild(child);
        }
        for (Map.Entry<String, List<Node>> stringListEntry : nodeLists.entrySet()) {
            var parent = new XMLNode(stringListEntry.getKey());
            var children = stringListEntry.getValue()
                    .stream()
                    .map(Node::toXML)
                    .toList();
            node = node.withChild(parent.withChildren(children));
        }
        return node;
    }
}