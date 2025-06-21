package magma.set;

import magma.JavaStream;
import magma.StreamLike;

import java.util.HashSet;
import java.util.Set;

public record JavaSet<Value>(Set<Value> set) implements SetLike<Value> {
    public JavaSet() {
        this(new HashSet<>());
    }

    @Override
    public StreamLike<Value> stream() {
        return new JavaStream<>(this.set.stream());
    }

    @Override
    public SetLike<Value> add(final Value value) {
        this.set.add(value);
        return this;
    }
}
