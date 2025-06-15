package magma.app.rule.or;

import magma.app.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record InlineOrState<Value>(Optional<Value> maybeValue, List<CompileError> errors) implements OrState<Value> {
    public InlineOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public <Return> Return match(Function<Value, Return> whenPresent, Function<List<CompileError>, Return> whenMissing) {
        return this.maybeValue.map(whenPresent).orElseGet(() -> whenMissing.apply(this.errors));
    }

    @Override
    public OrState<Value> withValue(Value value) {
        return new InlineOrState<>(Optional.of(value), this.errors);
    }

    @Override
    public OrState<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public boolean hasValue() {
        return this.maybeValue.isPresent();
    }
}