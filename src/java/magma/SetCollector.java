package magma;

import java.util.HashSet;
import java.util.Set;

public class SetCollector<Value> implements Collector<Value, Set<Value>> {
    @Override
    public Set<Value> createInitial() {
        return new HashSet<>();
    }

    @Override
    public Set<Value> fold(final Set<Value> values, final Value value) {
        values.add(value);
        return values;
    }
}
