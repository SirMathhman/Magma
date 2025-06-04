package magma.option;

/**
 * Option variant representing presence of a value.
 */
public final class Some<T> implements Option<T> {
    private final T value;

    Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }
}
