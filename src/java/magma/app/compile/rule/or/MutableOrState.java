package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class MutableOrState<Value, Error> implements OrState<Value, Error, Result<Value, Error>> {
    private final Optional<Value> maybeValue;
    private final List<Error> errors;
    private final ResultFactory<Value, Error> factory;

    MutableOrState(Optional<Value> maybeValue, List<Error> errors, ResultFactory<Value, Error> factory) {
        this.maybeValue = maybeValue;
        this.errors = errors;
        this.factory = factory;
    }

    public MutableOrState(ResultFactory<Value, Error> factory) {
        this(Optional.empty(), new ArrayList<>(), factory);
    }

    @Override
    public OrState<Value, Error, Result<Value, Error>> withValue(Value node) {
        return new MutableOrState<>(Optional.of(node), this.errors, this.factory);
    }

    @Override
    public OrState<Value, Error, Result<Value, Error>> withError(Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Result<Value, Error> toResult(Context context) {
        return this.maybeValue.map(this.factory::fromValue)
                .orElseGet(() -> this.factory.fromError(context));
    }
}
