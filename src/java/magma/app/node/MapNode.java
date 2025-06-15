package magma.app.node;

import magma.app.CompileError;
import magma.app.Node;
import magma.app.maybe.StringResult;
import magma.app.maybe.string.ErrStringResult;
import magma.app.maybe.string.OkStringResult;
import magma.app.rule.NodeContext;

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
    public StringResult findString(String key) {
        if (this.strings.containsKey(key))
            return new OkStringResult(this.strings.get(key));

        return new ErrStringResult(new CompileError("String '" + key + "' not present", new NodeContext(this)));
    }
}