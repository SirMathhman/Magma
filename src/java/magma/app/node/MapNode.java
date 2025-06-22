package magma.app.node;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

import java.util.HashMap;
import java.util.Map;

public final class MapNode implements EverythingNode {
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
    public EverythingNode withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Option<String> findString(final String key) {
        if (this.strings.containsKey(key))
            return new Some<>(this.strings.get(key));

        return new None<>();
    }

    @Override
    public String display() {
        return this.maybeType.map(type -> type + " ")
                .orElse("") + this.strings.toString();
    }

    @Override
    public EverythingNode retype(final String type) {
        return new MapNode(new Some<>(type), this.strings);
    }

    @Override
    public boolean is(final String type) {
        return this.maybeType.filter(inner -> inner.contentEquals(type))
                .isPresent();
    }
}