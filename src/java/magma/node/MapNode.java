package magma.node;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.HashMap;
import java.util.Map;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    private MapNode(final Map<String, String> strings) {
        this.strings = strings;
    }

    public MapNode() {
        this(new HashMap<>());
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Option<String> findString(final String key) {
        if (this.strings.containsKey(key))
            return new Some<>(this.strings.get(key));

        return new None<>();
    }
}