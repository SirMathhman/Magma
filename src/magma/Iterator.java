package magma;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Lightweight iterator interface used instead of {@link java.util.Iterator}.
 */
interface Iterator<T> {
    <R> Iterator<R> map(Function<T, R> mapper);

    <R> R fold(R initial, BiFunction<R, T, R> folder);

    <C> C collect(Collector<T, C> collector);

    <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper);

    Option<T> next();

    Iterator<T> filter(Predicate<T> predicate);

    <R> Iterator<Tuple<T, R>> zip(Iterator<R> other);
}
