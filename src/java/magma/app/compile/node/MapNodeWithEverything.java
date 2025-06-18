package magma.app.compile.node;

import magma.api.Tuple;
import magma.api.map.MapLike;
import magma.api.map.Maps;

import java.util.Optional;
import java.util.stream.Stream;

public final class MapNodeWithEverything implements NodeWithEverything {
    private final Optional<String> maybeType;
    private final MapLike<String, String> strings;
    private final MapLike<String, NodeWithEverything> nodes;

    public MapNodeWithEverything() {
        this(Optional.empty(), Maps.empty(), Maps.empty());
    }

    public MapNodeWithEverything(Optional<String> maybeType, MapLike<String, String> strings, MapLike<String, NodeWithEverything> nodes) {
        this.maybeType = maybeType;
        this.strings = strings;
        this.nodes = nodes;
    }

    public MapNodeWithEverything(String type) {
        this(Optional.of(type), Maps.empty(), Maps.empty());
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
        return this.strings.stream();
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
        return this.nodes.stream();
    }
}