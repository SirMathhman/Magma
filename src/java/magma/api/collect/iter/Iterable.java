package magma.api.collect.iter;

public interface Iterable<T> {
    default <Collection> Collection collect(Collector<T, Collection> collector) {
        return this.iter()
                .collect(collector);
    }

    Iter<T> iter();
}