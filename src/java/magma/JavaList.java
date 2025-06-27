package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Actual
public record JavaList<T>(List<T> list) implements ListLike<T> {
    public JavaList() {
        this(new ArrayList<>());
    }

    @Override
    public Stream<T> stream() {
        return this.list.stream();
    }

    @Override
    public ListLike<T> add(final T element) {
        this.list.add(element);
        return this;
    }

    @Override
    public Optional<Tuple<ListLike<T>, T>> popLast() {
        if (this.list.isEmpty())
            return Optional.empty();

        final var last = this.list.removeLast();
        return Optional.of(new Tuple<>(this, last));
    }
}
