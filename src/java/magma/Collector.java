package magma;

public interface Collector<Value, Collection> {
    Collection createInitial();

    Collection fold(Collection collection, Value value);
}
