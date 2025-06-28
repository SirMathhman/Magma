package magma;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Optional<T> {
    void ifPresent(Consumer<T> consumer);

    boolean isPresent();

    Optional<T> or(Supplier<Optional<T>> other);

    T orElseGet(Supplier<T> other);

    <R> Optional<R> map(Function<T, R> mapper);

    <R> Optional<R> flatMap(Function<T, Optional<R>> mapper);

    T orElse(T other);

    Optional<T> filter(Predicate<T> predicate);

    boolean isEmpty();

    Tuple<Boolean, T> toTuple(T other);

    Stream<T> stream();
}
