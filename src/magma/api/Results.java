package magma.api;

public class Results {
    public static <T, E> Result<T, E> $Result(Action<T> action) {
        try {
            return new Ok<>(action.perform());
        } catch (UnsafeException e) {
            //noinspection unchecked
            return Err.Err((E) e.getValue());
        }
    }

    public static <T, E extends Exception> T unwrapJava(Result<T, E> result) throws E {
        var value = result.findValue();
        if (value.isPresent()) return value.get();

        var error = result.findError();
        if (error.isPresent()) throw error.get();

        throw new RuntimeException("Malformed Result implementation, neither a value nor an error was present when unwrapping!");
    }
}
