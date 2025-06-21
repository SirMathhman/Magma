package magma;

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
