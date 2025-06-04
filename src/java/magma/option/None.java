package magma.option;

/**
 * Option variant representing absence of a value.
 */
public final class None<T> implements Option<T> {
    private static final None<?> INSTANCE = new None<>();

    private None() {}

    @SuppressWarnings("unchecked")
    static <T> None<T> instance() {
        return (None<T>) INSTANCE;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        throw new java.util.NoSuchElementException("No value present");
    }
}
