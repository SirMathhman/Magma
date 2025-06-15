package magma.app.compile.node;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeListResult;
import magma.app.compile.NodeResult;
import magma.app.compile.StringResult;
import magma.app.compile.rule.NodeContext;
import magma.app.compile.string.StringResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapNode implements Node {
    private final Map<String, String> strings;
    private final Map<String, List<Node>> nodeLists;

    public MapNode(Map<String, String> strings, Map<String, List<Node>> nodeLists) {
        this.strings = strings;
        this.nodeLists = nodeLists;
    }

    public MapNode() {
        this(new HashMap<>(), new HashMap<>());
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
    public NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key))
            return new NodeListOk(this.nodeLists.get(key));
        else
            return new NodeListErr<>(new CompileError("Node list '" + key + "' not present", new NodeContext<>(this)));
    }

    public Node withNodeList(String key, List<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public String display() {
        return this.toString();
    }
}