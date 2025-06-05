package magma.result;

/**
 * Result variant representing failure.
 * See {@code Ok} for the success case.
 */
public final class Err<T, X> implements Result<T, X> {
    private final X error;

    public Err(X error) {
        this.error = error;
    }

    public X error() {
        return error;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public <U> Result<U, X> mapValue(java.util.function.Function<? super T, ? extends U> mapper) {
        return new Err<>(error);
    }

    @Override
    public <U> Result<U, X> flatMapValue(java.util.function.Function<? super T, Result<U, X>> mapper) {
        return new Err<>(error);
    }

}
