package magma.option;

import java.util.function.Consumer;

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

    /**
     * Executes {@code action} if a value is present.
     */
    default void ifPresent(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(get());
        }
    }
}
