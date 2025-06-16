package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.error.FormattedError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MutableOrState<Value>(Optional<Value> maybeValue, List<FormattedError> errors) implements OrState<Value> {
    public MutableOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public Result<Value, List<FormattedError>> toResult() {
        return this.maybeValue.<Result<Value, List<FormattedError>>>map(Ok::new)
                .orElseGet(() -> new Err<>(this.errors));
    }

    @Override
    public OrState<Value> withValue(Value value) {
        if (this.maybeValue.isPresent())
            return this;
        return new MutableOrState<>(Optional.of(value), this.errors);
    }

    @Override
    public OrState<Value> withError(FormattedError error) {
        this.errors.add(error);
        return this;
    }
}