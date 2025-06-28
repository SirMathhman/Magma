package magma;

class ListCollector<T> implements Collector<T, ListLike<T>> {
    @Override
    public ListLike<T> createInitial() {
        return Lists.empty();
    }

    @Override
    public ListLike<T> fold(final ListLike<T> current, final T t) {
        return current.add(t);
    }
}
