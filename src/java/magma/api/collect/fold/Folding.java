package magma.api.collect.fold;

import java.util.function.BiFunction;

public interface Folding<T> {
    <R> R fold(R initial, BiFunction<R, T, R> folder);
}
