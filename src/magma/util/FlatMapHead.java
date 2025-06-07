package magma.util;

import java.util.function.Function;

public class FlatMapHead<T, R> implements Head<R> {
    private final Head<T> head;
    private final Function<T, Iterator<R>> mapper;
    private Iterator<R> current;

    public FlatMapHead(Iterator<R> initial, Head<T> head, Function<T, Iterator<R>> mapper) {
        this.current = initial;
        this.head = head;
        this.mapper = mapper;
    }

    @Override
    public Option<R> next() {
        while (true) {
            final var maybeNext = current.next();
            if (maybeNext.isPresent()) {
                return maybeNext;
            }

            final var maybeNextIter = head.next().map(mapper);
            if (maybeNextIter.isPresent()) {
                current = maybeNextIter.get();
            }
            else {
                return new None<>();
            }
        }
    }
}
