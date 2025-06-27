package magma;

import java.util.ArrayList;
import java.util.List;
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
            return new None<>();

        final var last = this.list.removeLast();
        return new Some<>(new Tuple<ListLike<T>, T>(this, last));
    }

    @Override
    public Optional<Tuple<T, ListLike<T>>> popFirst() {
        if (this.list.isEmpty())
            return new None<>();

        final var first = this.list.removeFirst();
        return new Some<>(new Tuple<T, ListLike<T>>(first, this));
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(final T element) {
        return this.list.contains(element);
    }
}
