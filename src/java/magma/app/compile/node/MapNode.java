package magma.app.compile.node;

import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

import java.util.HashMap;
import java.util.Map;

public record MapNode(
        Option<String> type,
        Map<String, String> strings,
        Map<String, Node> nodes,
        Map<String, List<Node>> nodeLists) implements Node {
    public MapNode(String type) {
        this(new Some<>(type), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public MapNode() {
        this(new None<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public boolean is(String type) {
        return this.type
                .filter((String inner) -> inner.equals(type))
                .isPresent();
    }

    @Override
    public Node retype(String type) {
        return new MapNode(new Some<>(type), this.strings, this.nodes, this.nodeLists);
    }

    @Override
    public Option<Node> findNode(String key) {
        if (this.nodes.containsKey(key)) {
            return new Some<>(this.nodes.get(key));
        }
        else {
            return new None<>();
        }
    }

    @Override
    public Option<String> findString(String key) {
        if (this.strings.containsKey(key)) {
            return new Some<>(this.strings.get(key));
        }
        else {
            return new None<>();
        }
    }

    public MapNode withNode(String key, Node value) {
        this.nodes.put(key, value);
        return this;
    }

    public MapNode withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Option<List<Node>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key)) {
            return new Some<>(this.nodeLists.get(key));
        }
        else {
            return new None<>();
        }
    }

    public MapNode withNodeList(String key, List<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }
}
