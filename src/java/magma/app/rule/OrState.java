package magma.app.rule;

import java.util.Optional;

public record OrState<Value>(Optional<Value> maybeValue) {
    public OrState() {
        this(Optional.empty());
    }

    public Optional<Value> unwrap() {
        return this.maybeValue;
    }

    public OrState<Value> withValue(Value value) {
        return new OrState<>(Optional.of(value));
    }
}
