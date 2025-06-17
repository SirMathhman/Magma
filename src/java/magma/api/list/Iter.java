package magma.api.list;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Iter<T> {
    <R> R fold(R initial, BiFunction<R, T, R> folder);

    Iter<T> filter(Predicate<T> predicate);

    <R> Iter<R> map(Function<T, R> mapper);

    default List<T> toList() {
        return this.collect(new ListCollector<>());
    }

    <C> C collect(Collector<T, C> collector);
}
