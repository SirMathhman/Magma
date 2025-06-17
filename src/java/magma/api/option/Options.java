package magma.api.option;

public class Options {
    public static <T> Option<T> empty() {
        return new None<>();
    }

    public static <T> Option<T> of(T value) {
        return new Some<>(value);
    }
}
