package magma.app.compile.node;

import magma.api.collect.map.MapLike;
import magma.api.collect.map.Maps;

import java.util.function.Function;
import java.util.function.Supplier;

public final class MapNode implements Node {
    private MapLike<String, String> strings;

    private MapNode(final MapLike<String, String> strings) {
        this.strings = strings;
    }

    public static Node empty() {
        return new MapNode(Maps.empty());
    }

    @Override
    public Node withString(final String key, final String value) {
        this.strings = this.strings.put(key, value);
        return this;
    }

    @Override
    public String asString() {
        return this.toString();
    }

    @Override
    public <Return> Return findStringOrElse(final String key, final Function<String, Return> ifPresent, final Supplier<Return> ifMissing) {
        return this.strings.findOrElse(key, ifPresent, ifMissing);
    }
}