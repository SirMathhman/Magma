package magma;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Optional value container with combinator style helpers.
 */
interface Option<T> {
    <R> Option<R> map(Function<T, R> mapper);

    T orElseGet(Supplier<T> other);

    boolean isPresent();

    T get();

    T orElse(T other);

    Option<T> or(Supplier<Option<T>> other);

    boolean isEmpty();

    <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
}
