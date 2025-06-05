package magma.option;

import magma.Tuple;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Minimal replacement for {@code java.util.Optional}.
 * Instances are either {@link Some} or {@link None}.
 */
public interface Option<T> {
    /**
     * Returns {@code true} if a value is present.
     */
    boolean isPresent();

    /**
     * Gets the contained value or raises an exception if absent.
     */
    T get();

    <U> Option<U> map(Function<? super T, ? extends U> mapper);

    <U> Option<U> flatMap(Function<? super T, Option<U>> mapper);

    Option<T> orElse(Option<T> other);

    Tuple<Boolean, T> toTuple(T defaultValue);

    /**
     * Executes {@code action} if a value is present.
     */
    default void ifPresent(Consumer<? super T> action) {
        Tuple<Boolean, T> tuple = toTuple(null);
        if (tuple.left()) {
            action.accept(tuple.right());
        }
    }
}
