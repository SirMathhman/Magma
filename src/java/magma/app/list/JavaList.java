package magma.app.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

record JavaList<Value>(List<Value> elements) implements ListLike<Value> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public Stream<Value> stream() {
        return this.elements.stream();
    }

    @Override
    public ListLike<Value> add(final Value element) {
        this.elements.add(element);
        return this;
    }
}
