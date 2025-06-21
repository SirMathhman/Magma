package magma;

public interface SetLike<Value> {
    StreamLike<Value> stream();

    SetLike<Value> add(Value value);
}
