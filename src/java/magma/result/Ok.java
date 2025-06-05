package magma.result;

/**
 * Result variant representing success.
 * See {@code Err} for the failure case.
 */
public final class Ok<T, X> implements Result<T, X> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public <U> Result<U, X> mapValue(java.util.function.Function<? super T, ? extends U> mapper) {
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public <U> Result<U, X> flatMapValue(java.util.function.Function<? super T, Result<U, X>> mapper) {
        return mapper.apply(value);
    }

}
