package magma.app.rule;

import magma.app.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OrState<Value>(Optional<Value> maybeValue, List<CompileError> errors) {
    public OrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    <Return> Return match(Function<Value, Return> whenPresent, Function<List<CompileError>, Return> whenMissing) {
        return this.maybeValue.map(whenPresent).orElseGet(() -> whenMissing.apply(this.errors));
    }

    public OrState<Value> withValue(Value value) {
        return new OrState<>(Optional.of(value), this.errors);
    }

    public OrState<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }

    public boolean hasValue() {
        return this.maybeValue.isPresent();
    }
}