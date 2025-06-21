package magma.api.collect.stream;

import magma.api.collect.set.SetLike;
import magma.api.collect.set.Sets;

public class SetCollector<Value> implements Collector<Value, SetLike<Value>> {
    @Override
    public SetLike<Value> createInitial() {
        return Sets.empty();
    }

    @Override
    public SetLike<Value> fold(final SetLike<Value> values, final Value value) {
        return values.add(value);
    }
}
