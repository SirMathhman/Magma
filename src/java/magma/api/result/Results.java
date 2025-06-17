package magma.api.result;

public class Results {
    public static <T, X> Result<T, X> fromErr(X error) {
        return new Err<>(error);
    }

    public static <T, X> Result<T, X> fromValue(T value) {
        return new Ok<>(value);
    }
}
