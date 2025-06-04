package magma;

/** Result variant representing success. */
public final class Ok<T, X extends Exception> implements Result<T, X> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }
}
