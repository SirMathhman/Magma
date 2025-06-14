package magma.app.node.complete;

import magma.app.node.Properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CompletingProperties<T, N> implements Properties<N, T> {
    private final Map<String, T> map;
    private final Completer<T, N> completer;

    public CompletingProperties(Completer<T, N> completer) {
        this(new HashMap<>(), completer);
    }

    private CompletingProperties(Map<String, T> map, Completer<T, N> completer) {
        this.map = map;
        this.completer = completer;
    }

    @Override
    public N with(String key, T value) {
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
    public Properties<N, T> merge(Properties<N, T> other) {
        return other.stream().<Properties<N, T>>reduce(this, (current, entry) -> current.add(entry.getKey(), entry.getValue()), (_, next) -> next);
    }

    @Override
    public Properties<N, T> add(String key, T value) {
        this.map.put(key, value);
        return this;
    }
}
