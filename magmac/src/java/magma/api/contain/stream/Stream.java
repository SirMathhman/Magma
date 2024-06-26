package magma.api.contain.stream;

import magma.api.Tuple;
import magma.api.contain.collect.Collector;
import magma.api.option.Option;
import magma.api.result.Result;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Stream<T> extends Head<T> {
    <R> Stream<R> map(Function<T, R> mapper);

    <C> C collect(Collector<T, C> collector);

    <C> C foldLeft(C current, BiFunction<C, T, C> folder);

    Option<T> head();

    <R, E> Result<R, E> foldLeftToResult(R initial, BiFunction<R, T, Result<R, E>> mapper);

    boolean anyMatch(Predicate<T> predicate);

    Stream<T> filter(Predicate<T> filter);

    <R> Stream<R> flatMap(Function<T, Head<R>> mapper);

    <R> Stream<Tuple<T, R>> extend(Function<T, R> mapper);

    default <R> R into(Function<Stream<T>, R> mapper) {
        return mapper.apply(this);
    }
}
