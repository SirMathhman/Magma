package magma.option;

public record Some<T>(T value) implements Option<T> {
}
