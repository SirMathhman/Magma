package magma.node;

import magma.api.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements Node {
    private final Map<String, String> strings;
    private final Map<String, List<Node>> nodeLists;
    private Optional<String> type;

    public MapNode() {
        this.type = Optional.empty();
        this.strings = new HashMap<>();
        this.nodeLists = new HashMap<>();
    }

    @Override
    public Stream<Tuple<String, String>> streamStrings() {
        return this.strings.entrySet().stream().map(entry -> {
            final var key = entry.getKey();
            final var value = entry.getValue();
            return new Tuple<>(key, value);
        });
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(final String key) {
        final var maybeValue = this.strings.get(key);
        return Optional.ofNullable(maybeValue);
    }

    @Override
    public Node merge(final Node other) {
        return other.streamStrings().<Node>reduce(this, (node, tuple) -> {
            final var key = tuple.left();
            final var value = tuple.right();
            return node.withString(key, value);
        }, (_, next) -> next);
    }

    @Override
    public Node retype(final String type) {
        this.type = Optional.of(type);
        return this;
    }

    @Override
    public boolean is(final String type) {
        return this.type.isPresent() && this.type.get().contentEquals(type);
    }

    @Override
    public Node withNodeList(final String key, final List<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public Optional<List<Node>> findNodeList(final String key) {
        return Optional.ofNullable(this.nodeLists.get(key));
    }
}