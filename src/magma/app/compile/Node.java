package magma.app.compile;

import magma.api.Tuple;
import magma.api.XMLNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Node {
    public static final String DEFAULT_TAG_NAME = "unknown";
    private final Map<String, String> strings;
    private final Map<String, List<String>> stringLists;
    private final Map<String, Node> nodes;
    private final Optional<String> type;
    private final Map<String, List<Node>> nodeLists;

    public Node() {
        this(Optional.empty(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    public Node(Optional<String> type, Map<String, String> strings, Map<String, List<String>> stringLists, Map<String, Node> nodes, Map<String, List<Node>> nodeLists) {
        this.type = type;
        this.strings = strings;
        this.nodes = nodes;
        this.nodeLists = nodeLists;
        this.stringLists = stringLists;
    }

    public Node(String type) {
        this(Optional.of(type), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public String toString() {
        return format();
    }

    public Node withString(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy, stringLists, nodes, nodeLists);
    }

    public Optional<String> findString(String propertyKey) {
        return Optional.ofNullable(strings.get(propertyKey));
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings, stringLists, nodes, nodeLists);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }

    public Node merge(Node other) {
        var stringsCopy = new HashMap<>(strings);
        stringsCopy.putAll(other.strings);

        var stringListsCopy = new HashMap<>(stringLists);
        stringListsCopy.putAll(other.stringLists);

        var nodesCopy = new HashMap<>(nodes);
        nodesCopy.putAll(other.nodes);

        var nodeListsCopy = new HashMap<>(nodeLists);
        nodeListsCopy.putAll(other.nodeLists);

        return new Node(type, stringsCopy, stringListsCopy, nodesCopy, nodeListsCopy);
    }

    public Optional<Node> findNode(String propertyKey) {
        return Optional.ofNullable(nodes.get(propertyKey));
    }

    public Node withNode(String propertyKey, Node propertyValue) {
        var copy = new HashMap<>(nodes);
        copy.put(propertyKey, propertyValue);
        return new Node(type, strings, stringLists, copy, nodeLists);
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
        return new Node(type, strings, stringLists, nodes, copy);
    }

    public Optional<List<Node>> findNodeList(String propertyKey) {
        return Optional.ofNullable(nodeLists.get(propertyKey));
    }

    public String format() {
        return toXML().format();
    }

    public XMLNode toXML() {
        var node = new XMLNode(type.orElse(DEFAULT_TAG_NAME));
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            node = node.withAttribute(entry.getKey(), entry.getValue());
        }

        for (var entry : stringLists.entrySet()) {
            var list = new XMLNode("string-list")
                    .withAttribute("for", entry.getKey());

            for (String childValue : entry.getValue()) {
                list = list.withChild(new XMLNode("child").withAttribute("value", childValue));
            }

            node = node.withChild(list);
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

    public Node withStringList(String propertyKey, List<String> values) {
        var copy = new HashMap<>(stringLists);
        copy.put(propertyKey, values);
        return new Node(type, strings, copy, nodes, nodeLists);
    }

    public Optional<List<String>> findStringList(String propertyKey) {
        return Optional.ofNullable(stringLists.get(propertyKey));
    }

    public Node removeString(String propertyKey) {
        var copy = new HashMap<>(strings);
        copy.remove(propertyKey);
        return new Node(type, copy, stringLists, nodes, nodeLists);
    }

    public Node removeStringList(String propertyKey) {
        var copy = new HashMap<>(stringLists);
        copy.remove(propertyKey);
        return new Node(type, strings, copy, nodes, nodeLists);
    }

    public Stream<Tuple<String, Node>> streamNodes() {
        return nodes.entrySet()
                .stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }

    public Stream<Tuple<String, List<Node>>> streamNodeLists() {
        return nodeLists.entrySet()
                .stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }

    public Node removeNodeList(String propertyKey) {
        var copy = new HashMap<>(nodeLists);
        copy.remove(propertyKey);
        return new Node(type, strings, stringLists, nodes, copy);
    }
}