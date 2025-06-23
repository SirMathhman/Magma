package magma.api.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record JavaList<T>(List<T> list) implements ListLike<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public boolean contains(final T element) {
        return list.contains(element);
    }

    @Override
    public Stream<T> stream() {
        return list.stream();
    }

    @Override
    public ListLike<T> add(final T element) {
        list.add(element);
        return this;
    }
}
