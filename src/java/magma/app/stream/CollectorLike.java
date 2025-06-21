package magma.app.stream;

public interface CollectorLike<Value, Collection> {
    Collection createInitial();

    Collection fold(Collection current, Value value);
}
