package magma.api.collect.head;

import magma.api.collect.iter.Collector;
import magma.api.collect.iter.Iter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public record HeadedIter<T>(Head<T> head) implements Iter<T> {
    @Override
    public <R> R fold(R initial, BiFunction<R, T, R> folder) {
        var current = initial;
        while (true) {
            R finalCurrent = current;
            final var optional = this.head.next()
                    .map(next -> folder.apply(finalCurrent, next));

            if (optional.isPresent())
                current = optional.get();
            else
                return current;
        }
    }

    @Override
    public Iter<T> filter(Predicate<T> predicate) {
        return new HeadedIter<>(new FilterHead<>(predicate, this.head));
    }

    @Override
    public <R> Iter<R> map(Function<T, R> mapper) {
        return new HeadedIter<>(() -> this.head.next()
                .map(mapper));
    }

    @Override
    public <C> C collect(Collector<T, C> collector) {
        return this.fold(collector.createInitial(), collector::fold);
    }
}
