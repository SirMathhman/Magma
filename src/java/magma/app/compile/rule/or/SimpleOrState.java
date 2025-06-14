package magma.app.compile.rule.or;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.error.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SimpleOrState<Value>(Optional<Value> maybeValue, List<CompileError> errors) implements OrState<Value> {
    public SimpleOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public OrState<Value> withValue(Value value) {
        if (this.maybeValue.isPresent())
            return this;

        return new SimpleOrState<>(Optional.of(value), this.errors);
    }

    @Override
    public OrState<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Result<Value, CompileError> toResult(Context context) {
        return this.maybeValue.<Result<Value, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError("No valid combination present", context)));
    }
}
