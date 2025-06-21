package magma.api.collect.list;

import magma.api.collect.stream.JavaStream;
import magma.api.collect.stream.StreamLike;

import java.util.ArrayList;
import java.util.List;

public record JavaList<Value>(List<Value> elements) implements ListLike<Value> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public StreamLike<Value> stream() {
        return new JavaStream<>(this.elements.stream());
    }

    @Override
    public ListLike<Value> add(final Value element) {
        this.elements.add(element);
        return this;
    }
}
