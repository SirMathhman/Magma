package magma.app.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Optional<String> maybeType;
    private final Map<String, String> strings;

    public MapNode() {
        this(Optional.empty(), new HashMap<>());
    }

    public MapNode(Optional<String> maybeType, Map<String, String> strings) {
        this.maybeType = maybeType;
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
        return Optional.empty();
    }

    @Override
    public Node retype(String type) {
        return new MapNode(Optional.of(type), this.strings);
    }

    @Override
    public boolean is(String type) {
        return this.maybeType.isPresent() && this.maybeType.get()
                .equals(type);
    }
}