package magma.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record JavaList<Value>(List<Value> list) implements ListLike<Value> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public ListLike<Value> add(final Value element) {
        list.add(element);
        return this;
    }

    @Override
    public Stream<Value> stream() {
        return list.stream();
    }

    @Override
    public Iterator<Value> iterator() {
        return list.iterator();
    }
}
