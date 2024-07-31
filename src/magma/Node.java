package magma;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Node {
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    private final Map<String, String> strings;
    private final Optional<String> type;

    public Node() {
        this(Optional.empty(), Collections.emptyMap());
    }

    public Node(Optional<String> type, Map<String, String> strings) {
        this.strings = strings;
        this.type = type;
    }

    public Node with(String propertyKey, String propertyValue) {
        var copy = new HashMap<>(strings);
        copy.put(propertyKey, propertyValue);
        return new Node(type, copy);
    }

    public Optional<String> findString(String propertyKey) {
        return Optional.ofNullable(strings.get(propertyKey));
    }

    public Node retype(String type) {
        return new Node(Optional.of(type), strings);
    }

    public boolean is(String type) {
        return this.type.isPresent() && this.type.get().equals(type);
    }
}