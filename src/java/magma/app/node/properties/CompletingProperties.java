package magma.app.node.properties;

import magma.app.node.CompoundNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class CompletingProperties<T> implements Properties<CompoundNode, T> {
    private final Map<String, T> map;
    private final Completer<T> completer;

    CompletingProperties(Completer<T> completer) {
        this(new HashMap<>(), completer);
    }

    private CompletingProperties(Map<String, T> map, Completer<T> completer) {
        this.map = map;
        this.completer = completer;
    }

    @Override
    public CompoundNode with(String key, T value) {
        this.map.put(key, value);
        return this.completer.complete(this);
    }

    @Override
    public Optional<T> find(String key) {
        if (this.map.containsKey(key))
            return Optional.of(this.map.get(key));
        else
            return Optional.empty();
    }

    @Override
    public Stream<Map.Entry<String, T>> stream() {
        return this.map.entrySet().stream();
    }

    @Override
    public Properties<CompoundNode, T> merge(Properties<CompoundNode, T> other) {
        return other.stream().<Properties<CompoundNode, T>>reduce(this, (current, entry) -> current.add(entry.getKey(), entry.getValue()), (_, next) -> next);
    }

    @Override
    public Properties<CompoundNode, T> add(String key, T value) {
        this.map.put(key, value);
        return this;
    }
}
