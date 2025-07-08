package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
}