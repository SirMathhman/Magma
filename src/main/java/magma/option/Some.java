package magma.option;

/** Holds a present value. */
public final class Some<T> implements Option<T> {
    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }
}
