package magma.app.compile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record Node(Map<String, String> strings) {
    public Node() {
        this(Collections.emptyMap());
    }

    Optional<String> findString(String key) {
        return Optional.ofNullable(strings().get(key));
    }

    public Node withString(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(copy);
    }

    public Node merge(Node other) {
        var copy = new HashMap<>(strings);
        copy.putAll(other.strings);
        return new Node(copy);
    }

    public Node mapString(String propertyKey, Function<String, String> mapper) {
        return findString(propertyKey)
                .map(mapper)
                .map(value -> withString(propertyKey, value))
                .orElse(this);
    }
}