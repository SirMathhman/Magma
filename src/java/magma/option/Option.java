package magma.option;

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
     * Convenience method returning the negation of {@link #isPresent()}.
     */
    default boolean isEmpty() {
        return !isPresent();
    }

    /**
     * Gets the contained value or throws if absent.
     */
    T get();

    /**
     * Executes {@code action} if a value is present.
     */
    default void ifPresent(java.util.function.Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(get());
        }
    }

    /**
     * Returns an {@link Option} containing {@code value}.
     */
    static <T> Option<T> some(T value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        return new Some<>(value);
    }

    /**
     * Returns the singleton {@link None} instance.
     */
    static <T> Option<T> none() {
        return None.instance();
    }
}
