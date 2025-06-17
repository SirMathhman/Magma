package magma.app.compile.rule.or;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record MutableAccumulator<Value, Error>(Optional<Value> maybeValue,
                                        List<Error> errors) implements Accumulator<Value, Error> {
    public MutableAccumulator() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public Accumulator<Value, Error> withValue(Value node) {
        return new MutableAccumulator<>(Optional.of(node), this.errors);
    }

    @Override
    public Accumulator<Value, Error> withError(Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Result<Value, List<Error>> toResult() {
        return this.maybeValue.<Result<Value, List<Error>>>map(Ok::new)
                .orElseGet(() -> new Err<>(this.errors));
    }
}
