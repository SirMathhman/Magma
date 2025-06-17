package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record MutableOrState<T>(Optional<T> maybeValue,
                         List<FormattedError> errors) implements OrState<T, FormattedError, Result<T, FormattedError>> {
    public MutableOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public OrState<T, FormattedError, Result<T, FormattedError>> withValue(T node) {
        return new MutableOrState<>(Optional.of(node), this.errors);
    }

    @Override
    public OrState<T, FormattedError, Result<T, FormattedError>> withError(FormattedError error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Optional<T> maybeValue() {
        return this.maybeValue;
    }
}
