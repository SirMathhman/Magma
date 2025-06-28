package magma;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Stream<Value> {
    static <T> Stream<T> of(final T... values) {
        return Stream.fromArray(values);
    }

    static <T> Stream<T> fromArray(final T[] values) {
        return new HeadedStream<>(new RangeHead(values.length)).map(index -> values[index]);
    }

    static <T> Stream<T> empty() {
        return new HeadedStream<>(new EmptyHead<>());
    }

    <R> Stream<R> map(Function<Value, R> mapper);

    <C> C collect(Collector<Value, C> collector);

    <R> Stream<R> flatMap(Function<Value, Stream<R>> mapper);

    Stream<Value> filter(Predicate<Value> predicate);

    Optional<Value> next();
}
