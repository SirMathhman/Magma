package magma.api.collect.list;

import magma.api.collect.stream.Collector;

public class ListCollector<T> implements Collector<T, ListLike<T>> {
    @Override
    public ListLike<T> createInitial() {
        return Lists.empty();
    }

    @Override
    public ListLike<T> fold(final ListLike<T> list, final T element) {
        return list.add(element);
    }
}
