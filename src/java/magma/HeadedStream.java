package magma;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class HeadedStream<Value> implements Stream<Value> {
    private final Head<Value> head;

    public HeadedStream(final Head<Value> head) {
        this.head = head;
    }

    @Override
    public <R> Stream<R> map(final Function<Value, R> mapper) {
        return new HeadedStream<>(() -> this.head.next().map(mapper));
    }

    @Override
    public <C> C collect(final Collector<Value, C> collector) {
        return this.fold(collector.createInitial(), collector::fold);
    }

    private <Return> Return fold(final Return initial, final BiFunction<Return, Value, Return> folder) {
        var current = initial;
        while (true) {
            final Return finalCurrent = current;
            final var tuple = this.head.next().map(next -> folder.apply(finalCurrent, next)).toTuple(current);
            if (tuple.left())
                current = tuple.right();
            else
                break;
        }

        return current;
    }

    @Override
    public <Return> Stream<Return> flatMap(final Function<Value, Stream<Return>> mapper) {
        return this.head.next().map(mapper).<Stream<Return>>map(initial -> {
            return new HeadedStream<>(new Head<Return>() {
                private Stream<Return> current = initial;

                @Override
                public Optional<Return> next() {
                    while (true) {
                        final var maybe = this.current.next();
                        if (maybe.isPresent())
                            return maybe;

                        final var map = HeadedStream.this.head.next().map(mapper).toTuple(this.current);
                        if (map.left())
                            this.current = map.right();
                        else
                            return new None<>();
                    }
                }
            });
        }).orElseGet(() -> Stream.empty());
    }

    @Override
    public Stream<Value> filter(final Predicate<Value> predicate) {
        return this.flatMap(value -> {
            if (predicate.test(value))
                return Stream.of(value);
            return Stream.empty();
        });
    }

    @Override
    public Optional<Value> next() {
        return this.head.next();
    }
}
