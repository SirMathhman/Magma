package magma.util;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
    @Override
    public <R> Iterator<R> map(Function<T, R> mapper) {
        return new HeadedIterator<>(() -> head.next().map(mapper));
    }

    @Override
    public <R> R fold(R initial, BiFunction<R, T, R> folder) {
        var current = initial;
        while (true) {
            R finalCurrent = current;
            final var maybeNext = head.next().map(next -> folder.apply(finalCurrent, next));
            if (maybeNext.isPresent()) {
                current = maybeNext.get();
            }
            else {
                return current;
            }
        }
    }

    @Override
    public <C> C collect(Collector<T, C> collector) {
        return fold(collector.createInitial(), collector::fold);
    }

    @Override
    public <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
        final var head = this.head.next()
                .map(mapper)
                .<Head<R>>map(initial -> new FlatMapHead<>(initial, this.head, mapper))
                .orElseGet(EmptyHead::new);

        return new HeadedIterator<>(head);
    }

    @Override
    public Option<T> next() {
        return head.next();
    }

    @Override
    public Iterator<T> filter(Predicate<T> predicate) {
        return flatMap(element -> {
            final var isValid = predicate.test(element);
            final var head = isValid ? new SingleHead<>(element) : new EmptyHead<T>();
            return new HeadedIterator<>(head);
        });
    }

    @Override
    public <R> Iterator<Tuple<T, R>> zip(Iterator<R> other) {
        return new HeadedIterator<>(() -> head.next().and(() -> other.next()));
    }
}
