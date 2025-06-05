package magma.result;

/** Utility methods for working with {@link Result}. */
public final class Results {
    private Results() {}

    /**
     * Unwraps the given result. If the result is {@code Ok}, the contained
     * value is returned. If it is {@code Err}, a {@link RuntimeException}
     * wrapping the error is thrown.
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Exception> T unwrap(Result<T, X> result) {
        if (result.isOk()) {
            return ((Ok<T, X>) result).value();
        }
        Err<T, X> err = (Err<T, X>) result;
        throw new RuntimeException(err.error());
    }
}
