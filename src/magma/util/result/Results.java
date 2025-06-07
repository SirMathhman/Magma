package magma.util.result;

/** Utility methods for working with {@link Result}. */
public final class Results {
    private Results() {}

    @SuppressWarnings("unchecked")
    public static <T, X> T unwrap(Result<T, X> result) {
        if (result.isOk()) {
            return ((Ok<T, X>) result).value();
        }
        Err<T, X> err = (Err<T, X>) result;
        throw new RuntimeException(String.valueOf(err.error()));
    }
}
