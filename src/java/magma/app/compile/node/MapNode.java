package magma.app.compile.node;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.StringResult;
import magma.app.compile.string.StringResults;

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
    public StringResult<CompileError> findString(String key) {
        if (this.strings.containsKey(key))
            return StringResults.createFromValue(this.strings.get(key));
        return StringResults.createFromNode("String '" + key + "' not present", this);
    }

    @Override
    public String display() {
        return this.toString();
    }
}