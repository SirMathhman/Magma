package magma;

class EmptyHead<T> implements Head<T> {
    @Override
    public Option<T> next() {
        return new None<>();
    }
}
