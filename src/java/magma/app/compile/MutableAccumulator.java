package magma.app.compile;

import magma.api.list.List;
import magma.api.list.Lists;
import magma.api.list.Streamable;

import java.util.Optional;
import java.util.function.Function;

record MutableAccumulator<Value, Error>(Optional<Value> maybeValue,
                                        List<Error> errors) implements Accumulator<Value, Error, Streamable<Error>> {
    public MutableAccumulator() {
        this(Optional.empty(), Lists.empty());
    }

    @Override
    public Accumulator<Value, Error, Streamable<Error>> withValue(Value node) {
        return new MutableAccumulator<>(Optional.of(node), this.errors);
    }

    @Override
    public Accumulator<Value, Error, Streamable<Error>> withError(Error error) {
        return new MutableAccumulator<>(this.maybeValue, this.errors.add(error));
    }

    @Override
    public <Result extends AttachableToStateResult<Value, Error>> Result match(Function<Value, Result> whenOk, Function<Streamable<Error>, Result> whenErr) {
        return this.maybeValue.map(whenOk)
                .orElseGet(() -> whenErr.apply(this.errors));
    }
}
