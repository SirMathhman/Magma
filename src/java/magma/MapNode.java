package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class MapNode implements Node {
    private final Map<String, String> strings = new HashMap<>();

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(final String key) {
        return Optional.ofNullable(this.strings.get(key));
    }

    @Override
    public Stream<Map.Entry<String, String>> stream() {return this.strings.entrySet().stream();}

    @Override
    public Node merge(final Node other) {
        return other.stream()
                    .<Node>reduce(this, (node, entry) -> node.withString(entry.getKey(), entry.getValue()),
                                  (_, next) -> next);
    }
}