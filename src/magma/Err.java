package magma;

/** Result variant representing failure. */
public final class Err<T, X extends Exception> implements Result<T, X> {
    private final X error;

    public Err(X error) {
        this.error = error;
    }

    public X error() {
        return error;
    }
}
