package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.error.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MutableOrState<Value>(Optional<Value> maybeValue, List<CompileError> errors) implements OrState<Value> {
    public MutableOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public Result<Value, List<CompileError>> toResult() {
        return this.maybeValue.<Result<Value, List<CompileError>>>map(Ok::new)
                .orElseGet(() -> new Err<>(this.errors));
    }

    @Override
    public OrState<Value> withValue(Value value) {
        if (this.maybeValue.isPresent())
            return this;
        return new MutableOrState<>(Optional.of(value), this.errors);
    }

    @Override
    public OrState<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }
}