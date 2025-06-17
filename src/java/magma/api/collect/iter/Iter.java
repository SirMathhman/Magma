package magma.api.collect.iter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Iter<T> {
    <R> R fold(R initial, BiFunction<R, T, R> folder);

    Iter<T> filter(Predicate<T> predicate);

    <R> Iter<R> map(Function<T, R> mapper);

    <C> C collect(Collector<T, C> collector);
}
