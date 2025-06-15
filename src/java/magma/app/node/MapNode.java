package magma.app.node;

import magma.app.maybe.MaybeString;
import magma.app.Node;
import magma.app.maybe.string.EmptyString;
import magma.app.maybe.string.PresentString;

import java.util.HashMap;
import java.util.Map;

public final class MapNode implements Node {
    private final Map<String, String> strings;

    public MapNode(Map<String, String> strings) {
        this.strings = strings;
    }

    public MapNode() {
        this(new HashMap<>());
    }

    @Override
    public Node withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public MaybeString findString(String key) {
        if (this.strings.containsKey(key))
            return new PresentString(this.strings.get(key));
        return new EmptyString();
    }
}