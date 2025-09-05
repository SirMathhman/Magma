package magma;

public final class Err<T, E> implements Result<T, E> {
    private final E error;

    public Err(E error) {
        this.error = error;
    }

    public E getError() {
        return error;
    }
}
