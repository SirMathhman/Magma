package magma;

public class EmptyHead<T> implements Head<T> {
    @Override
    public Optional<T> next() {
        return new None<>();
    }
}
