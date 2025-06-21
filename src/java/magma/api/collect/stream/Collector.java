package magma.api.collect.stream;

public interface Collector<Value, Collection> {
    Collection createInitial();

    Collection fold(Collection collection, Value value);
}
