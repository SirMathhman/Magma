package magma.app.compile;

import magma.api.list.Iter;
import magma.api.list.JavaList;
import magma.api.list.Streamable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Optional<String> maybeType;
    private final Map<String, String> strings;
    private final Map<String, Streamable<Node>> nodeLists;

    public MapNode() {
        this(Optional.empty(), new HashMap<>(), new HashMap<>());
    }

    public MapNode(Optional<String> maybeType, Map<String, String> strings, Map<String, Streamable<Node>> nodeLists) {
        this.maybeType = maybeType;
        this.strings = strings;
        this.nodeLists = nodeLists;
    }

    @Override
    public Node withString(String key, String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(String key) {
        if (this.strings.containsKey(key))
            return Optional.of(this.strings.get(key));
        else
            return Optional.empty();
    }

    @Override
    public Node merge(Node other) {
        return other.streamStrings()
                .<Node>fold(this, (node, entry) -> node.withString(entry.getKey(), entry.getValue()));
    }

    @Override
    public Iter<Map.Entry<String, String>> streamStrings() {
        return new JavaList<>(new ArrayList<>(this.strings.entrySet())).stream();
    }

    @Override
    public Node withNodeList(String key, Streamable<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public Optional<Streamable<Node>> findNodeList(String key) {
        if (this.nodeLists.containsKey(key))
            return Optional.of(this.nodeLists.get(key));
        else
            return Optional.empty();
    }

    @Override
    public String display() {
        return this.strings.toString() + this.nodeLists.toString();
    }

    @Override
    public boolean is(String type) {
        return this.maybeType.isPresent() && this.maybeType.get()
                .equals(type);
    }

    @Override
    public Node retype(String type) {
        return new MapNode(Optional.of(type), this.strings, this.nodeLists);
    }
}