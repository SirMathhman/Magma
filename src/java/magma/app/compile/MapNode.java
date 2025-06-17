package magma.app.compile;

import magma.api.collect.iter.Collector;
import magma.api.collect.iter.Iterable;
import magma.api.collect.list.JavaList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
    private final Optional<String> maybeType;
    private final Map<String, String> strings;
    private final Map<String, Iterable<Node>> nodeLists;

    public MapNode() {
        this(Optional.empty(), new HashMap<>(), new HashMap<>());
    }

    public MapNode(Optional<String> maybeType, Map<String, String> strings, Map<String, Iterable<Node>> nodeLists) {
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
        return other.collect(new StringNodeCollector(this));
    }

    @Override
    public Node withNodeList(String key, Iterable<Node> values) {
        this.nodeLists.put(key, values);
        return this;
    }

    @Override
    public Optional<Iterable<Node>> findNodeList(String key) {
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

    @Override
    public Node collect(Collector<Map.Entry<String, String>, Node> collector) {
        return new JavaList<>(new ArrayList<>(this.strings.entrySet())).iter()
                .collect(collector);
    }
}