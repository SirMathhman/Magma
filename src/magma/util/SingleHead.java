package magma.util;

public class SingleHead<T> implements Head<T> {
    private final T element;
    private boolean retrieved = false;

    public SingleHead(T element) {
        this.element = element;
    }

    @Override
    public Option<T> next() {
        if (retrieved) {
            return new None<>();
        }

        retrieved = true;
        return new Some<>(element);
    }
}
