package magma.list;

import java.util.List;
import java.util.stream.Stream;

public record JavaList<T>(List<T> list) implements ListLike<T> {
    @Override
    public boolean contains(final T element) {
        return this.list.contains(element);
    }

    @Override
    public Stream<T> stream() {
        return this.list.stream();
    }
}
