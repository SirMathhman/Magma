package magma.app.compile.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Node {
    private final Map<String, String> strings;
    private final Optional<String> type;

    public Node(Optional<String> type, Map<String, String> strings) {
        this.strings = strings;
        this.type = type;
    }

    public Node() {
        this(Optional.empty(), Collections.emptyMap());
    }

    @Override
    public String toString() {
        return "Node{" +
                "strings=" + strings +
                ", type=" + type +
                '}';
    }

    public Map<String, String> strings() {
        return strings;
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }

    public Node with(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy);
    }
}
