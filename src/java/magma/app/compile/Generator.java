package magma.app.compile;

public interface Generator<T> {
    String generate(T value);
}
