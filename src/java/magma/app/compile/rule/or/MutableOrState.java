package magma.app.compile.rule.or;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record MutableOrState<T>(Optional<T> maybeValue, List<FormattedError> errors) implements OrState<T> {
    public MutableOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public OrState<T> withValue(T node) {
        return new MutableOrState<>(Optional.of(node), this.errors);
    }

    @Override
    public OrState<T> withError(FormattedError error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Result<T, FormattedError> toResult(Context context) {
        return this.maybeValue.<Result<T, FormattedError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("No combination present", context)));
    }
}
