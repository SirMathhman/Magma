package magma.option;

/**
 * Option variant representing absence of a value.
 */
public final class None<T> implements Option<T> {
    None() {}

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        throw new java.util.NoSuchElementException("No value present");
    }
}
