package magma.api.collect.set;

import magma.api.collect.stream.StreamLike;

public interface SetLike<Value> {
    StreamLike<Value> stream();

    SetLike<Value> add(Value value);
}
