package magma;

public interface Collector<Value, Collection> {
    Collection createInitial();

    Collection fold(Collection current, Value value);
}
