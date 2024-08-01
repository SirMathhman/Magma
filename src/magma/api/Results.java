package magma.api;

public class Results {
    public static <T, E extends Exception> T unwrap(Result<T, E> result) throws E {
        var value = result.findValue();
        if (value.isPresent()) return value.get();

        var error = result.findError();
        if (error.isPresent()) throw error.get();

        throw new RuntimeException("Malformed Result implementation, neither a value nor an error was present when unwrapping!");
    }
}
