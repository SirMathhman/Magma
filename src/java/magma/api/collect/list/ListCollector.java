package magma.api.collect.list;

import magma.api.collect.iter.Collector;

public record ListCollector<T>() implements Collector<T, List<T>> {
    @Override
    public List<T> createInitial() {
        return Lists.empty();
    }

    @Override
    public List<T> fold(List<T> current, T element) {
        return current.add(element);
    }
}
