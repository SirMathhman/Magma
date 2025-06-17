package magma.app.compile;

import magma.api.List;

import java.util.Optional;
import java.util.function.Function;

record MutableAccumulator<Value, Error>(Optional<Value> maybeValue,
                                        List<Error> errors) implements Accumulator<Value, Error> {
    public MutableAccumulator() {
        this(Optional.empty(), List.empty());
    }

    @Override
    public Accumulator<Value, Error> withValue(Value node) {
        return new MutableAccumulator<>(Optional.of(node), this.errors);
    }

    @Override
    public Accumulator<Value, Error> withError(Error error) {
        return new MutableAccumulator<>(this.maybeValue, this.errors.add(error));
    }

    @Override
    public <Result extends AttachableToStateResult<Value, Error>> Result getMatch(Function<Value, Result> whenOk, Function<List<Error>, Result> whenErr) {
        return this.maybeValue.map(whenOk)
                .orElseGet(() -> whenErr.apply(this.errors));
    }
}
