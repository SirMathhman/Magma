package magma.app.compile.node.map;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.node.property.CompoundNode;

import java.util.HashMap;
import java.util.Map;

public final class MapNode implements CompoundNode {
    private final Option<String> maybeType;
    private final Map<String, String> strings;

    private MapNode(final Option<String> maybeType, final Map<String, String> strings) {
        this.maybeType = maybeType;
        this.strings = strings;
    }

    public MapNode() {
        this(new None<>(), new HashMap<>());
    }

    @Override
    public CompoundNode withString(final String key, final String value) {
        strings.put(key, value);
        return this;
    }

    @Override
    public Option<String> findString(final String key) {
        if (strings.containsKey(key))
            return new Some<>(strings.get(key));

        return new None<>();
    }

    @Override
    public String display() {
        return maybeType.map(type -> type + " ")
                .orElse("") + strings.toString();
    }

    @Override
    public CompoundNode retype(final String type) {
        return new MapNode(new Some<>(type), strings);
    }

    @Override
    public boolean is(final String type) {
        return maybeType.filter(inner -> inner.contentEquals(type))
                .isPresent();
    }
}