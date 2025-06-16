package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.error.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record State<Value>(Optional<Value> maybeValue, List<CompileError> errors) {
    public State() {
        this(Optional.empty(), new ArrayList<>());
    }

    public Result<Value, List<CompileError>> toResult() {
        return this.maybeValue.<Result<Value, List<CompileError>>>map(Ok::new)
                .orElseGet(() -> new Err<>(this.errors));
    }

    public State<Value> withValue(Value value) {
        if (this.maybeValue.isPresent())
            return this;
        return new State<>(Optional.of(value), this.errors);
    }

    public State<Value> withError(CompileError error) {
        this.errors.add(error);
        return this;
    }
}