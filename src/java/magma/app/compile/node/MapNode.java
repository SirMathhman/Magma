package magma.app.compile.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements Node {
    private final Optional<String> maybeType;
    private final Map<String, String> strings;
    private final Map<String, Node> nodes;

    public MapNode() {
        this(Optional.empty(), new HashMap<>(), new HashMap<>());
    }

    public MapNode(Optional<String> maybeType, Map<String, String> strings, Map<String, Node> nodes) {
        this.maybeType = maybeType;
        this.strings = strings;
        this.nodes = nodes;
    }

    public MapNode(String type) {
        this(Optional.of(type), new HashMap<>(), new HashMap<>());
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
    public Node retype(String type) {
        return new MapNode(Optional.of(type), this.strings, this.nodes);
    }

    @Override
    public boolean is(String type) {
        return this.maybeType.isPresent() && this.maybeType.get()
                .equals(type);
    }

    @Override
    public Node merge(Node other) {
        final var withStrings = other.streamStrings()
                .<Node>reduce(this,
                        (node1, entry1) -> node1.withString(entry1.getKey(), entry1.getValue()),
                        (_, next) -> next);

        return other.streamNodes()
                .reduce(withStrings, (node, entry) -> node.withNode(entry.getKey(), entry.getValue()),
                        (_, next) -> next);
    }

    @Override
    public Stream<Map.Entry<String, String>> streamStrings() {
        return this.strings.entrySet()
                .stream();
    }

    @Override
    public Optional<Node> findNode(String key) {
        if (this.nodes.containsKey(key))
            return Optional.of(this.nodes.get(key));
        else
            return Optional.empty();
    }

    @Override
    public Node withNode(String key, Node value) {
        this.nodes.put(key, value);
        return this;
    }

    @Override
    public Stream<Map.Entry<String, Node>> streamNodes() {
        return this.nodes.entrySet()
                .stream();
    }
}