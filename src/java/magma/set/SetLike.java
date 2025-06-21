package magma.set;

import magma.StreamLike;

public interface SetLike<Value> {
    StreamLike<Value> stream();

    SetLike<Value> add(Value value);
}
