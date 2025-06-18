package magma.app.compile.node;

import magma.api.Tuple;
import magma.api.list.Sequence;
import magma.api.map.MapLike;
import magma.api.map.Maps;

import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements NodeWithEverything {
    private final Optional<String> maybeType;
    private MapLike<String, String> strings;
    private MapLike<String, NodeWithEverything> nodes;
    private MapLike<String, Sequence<NodeWithEverything>> nodeLists;

    public MapNode() {
        this(Optional.empty(), Maps.empty(), Maps.empty(), Maps.empty());
    }

    public MapNode(Optional<String> maybeType, MapLike<String, String> strings, MapLike<String, NodeWithEverything> nodes, MapLike<String, Sequence<NodeWithEverything>> nodeLists) {
        this.maybeType = maybeType;
        this.strings = strings;
        this.nodes = nodes;
        this.nodeLists = nodeLists;
    }

    public MapNode(String type) {
        this(Optional.of(type), Maps.empty(), Maps.empty(), Maps.empty());
    }

    @Override
    public NodeWithEverything withString(String key, String value) {
        this.strings = this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(String key) {
        return this.strings.find(key);
    }

    @Override
    public NodeWithEverything retype(String type) {
        return new MapNode(Optional.of(type), this.strings, this.nodes, Maps.empty());
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
        return this.nodes.find(key);
    }

    @Override
    public NodeWithEverything withNode(String key, NodeWithEverything value) {
        this.nodes = this.nodes.put(key, value);
        return this;
    }

    @Override
    public Stream<Tuple<String, NodeWithEverything>> streamNodes() {
        return this.nodes.stream();
    }

    @Override
    public NodeWithEverything withNodeList(String key, Sequence<NodeWithEverything> values) {
        this.nodeLists = this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public Optional<Sequence<NodeWithEverything>> findNodeList(String key) {
        return this.nodeLists.find(key);
    }
}