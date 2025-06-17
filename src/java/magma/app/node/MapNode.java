package magma.app.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    public MapNode() {
        this(new HashMap<>());
    }

    public MapNode(Map<String, String> strings) {
        this.strings = strings;
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
        else
            return Optional.empty();
    }

    @Override
    public Node merge(Node other) {
        return other.streamStrings()
                .<Node>reduce(this, (node, entry) -> node.withString(entry.getKey(), entry.getValue()), (_, next) -> next);
    }

    @Override
    public Stream<Map.Entry<String, String>> streamStrings() {
        return this.strings.entrySet()
                .stream();
    }
}