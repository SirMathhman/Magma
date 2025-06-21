package jvm.list;

import jvm.stream.JavaStream;
import magma.app.StreamLike;
import magma.app.list.ListLike;

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
