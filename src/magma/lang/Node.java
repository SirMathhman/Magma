package magma.lang;

import magma.JavaList;

import java.util.Map;
import java.util.Optional;

public final class Node {
    public static final String DEPTH = "depth";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final String PARAMS = "params";
    public static final String CONTENT = "content";

    private final Map<String, Integer> integers;
    private final Map<String, String> strings;
    private final Map<String, JavaList<String>> stringLists;

    public Node(Map<String, Integer> integers, Map<String, String> strings, Map<String, JavaList<String>> stringLists) {
        this.integers = integers;
        this.strings = strings;
        this.stringLists = stringLists;
    }

    public Optional<Integer> findInteger(String propertyName) {
        return integers.containsKey(propertyName)
                ? Optional.of(integers.get(propertyName))
                : Optional.empty();
    }

    public Optional<String> findString(String propertyName) {
        return strings.containsKey(propertyName)
                ? Optional.of(strings.get(propertyName))
                : Optional.empty();
    }

    public Optional<JavaList<String>> findStringList(String propertyName) {
        return stringLists.containsKey(propertyName)
                ? Optional.of(stringLists.get(propertyName))
                : Optional.empty();
    }
}