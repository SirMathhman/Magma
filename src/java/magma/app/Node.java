package magma.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Node {
    private final Map<String, String> strings;

    public Node() {
        this(new HashMap<>());
    }

    public Node(Map<String, String> strings) {
        this.strings = strings;
    }

    public Node withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    public Optional<String> findString(String key) {
        if (this.strings.containsKey(key))
            return Optional.of(this.strings.get(key));

        return Optional.empty();
    }
}