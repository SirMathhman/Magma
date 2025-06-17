package magma.app.compile.rule.or;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record MutableOrState<Value, Error>(Optional<Value> maybeValue, List<Error> errors) implements OrState<Value, Error> {
    public MutableOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public OrState<Value, Error> withValue(Value node) {
        return new MutableOrState<>(Optional.of(node), this.errors);
    }

    @Override
    public OrState<Value, Error> withError(Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Optional<Value> maybeValue() {
        return this.maybeValue;
    }
}
