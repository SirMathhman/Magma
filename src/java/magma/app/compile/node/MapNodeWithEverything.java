package magma.app.compile.node;

import magma.api.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNodeWithEverything implements NodeWithEverything {
    private final Optional<String> maybeType;
    private final Map<String, String> strings;
    private final Map<String, NodeWithEverything> nodes;

    public MapNodeWithEverything() {
        this(Optional.empty(), new HashMap<>(), new HashMap<>());
    }

    public MapNodeWithEverything(Optional<String> maybeType, Map<String, String> strings, Map<String, NodeWithEverything> nodes) {
        this.maybeType = maybeType;
        this.strings = strings;
        this.nodes = nodes;
    }

    public MapNodeWithEverything(String type) {
        this(Optional.of(type), new HashMap<>(), new HashMap<>());
    }

    @Override
    public NodeWithEverything withString(String key, String value) {
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
    public NodeWithEverything retype(String type) {
        return new MapNodeWithEverything(Optional.of(type), this.strings, this.nodes);
    }

    @Override
    public boolean is(String type) {
        return this.maybeType.isPresent() && this.maybeType.get()
                .equals(type);
    }

    @Override
    public NodeWithEverything merge(NodeWithEverything other) {
        final var withStrings = other.streamStrings()
                .<NodeWithEverything>reduce(this, (node1, entry1) -> node1.withString(entry1.left(), entry1.right()),
                        (_, next) -> next);

        return other.streamNodes()
                .reduce(withStrings, (node, entry) -> node.withNode(entry.left(), entry.right()), (_, next) -> next);
    }

    @Override
    public Stream<Tuple<String, String>> streamStrings() {
        return this.strings.entrySet()
                .stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Optional<NodeWithEverything> findNode(String key) {
        if (this.nodes.containsKey(key))
            return Optional.of(this.nodes.get(key));
        else
            return Optional.empty();
    }

    @Override
    public NodeWithEverything withNode(String key, NodeWithEverything value) {
        this.nodes.put(key, value);
        return this;
    }

    @Override
    public Stream<Tuple<String, NodeWithEverything>> streamNodes() {
        return this.nodes.entrySet()
                .stream()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }
}