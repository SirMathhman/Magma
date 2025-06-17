package magma.app.compile.rule.or;

import magma.app.compile.context.Context;
import magma.app.compile.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class MutableOrState<Value, Error, Result> implements OrState<Value, Error, Result> {
    private final Optional<Value> maybeValue;
    private final List<Error> errors;
    private final ResultFactory<Value, Result> factory;

    MutableOrState(Optional<Value> maybeValue, List<Error> errors, ResultFactory<Value, Result> factory) {
        this.maybeValue = maybeValue;
        this.errors = errors;
        this.factory = factory;
    }

    public MutableOrState(ResultFactory<Value, Result> factory) {
        this(Optional.empty(), new ArrayList<>(), factory);
    }

    @Override
    public OrState<Value, Error, Result> withValue(Value node) {
        return new MutableOrState<>(Optional.of(node), this.errors, this.factory);
    }

    @Override
    public OrState<Value, Error, Result> withError(Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Result toResult(Context context) {
        return this.maybeValue.map(this.factory::fromValue)
                .orElseGet(() -> this.factory.fromError(context));
    }
}
