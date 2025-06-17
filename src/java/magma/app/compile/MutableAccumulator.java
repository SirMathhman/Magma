package magma.app.compile;

import magma.api.collect.iter.Iterable;
import magma.api.collect.list.List;
import magma.api.collect.list.Lists;

import java.util.Optional;
import java.util.function.Function;

record MutableAccumulator<Value, Error>(Optional<Value> maybeValue,
                                        List<Error> errors) implements Accumulator<Value, Error, Iterable<Error>> {
    public MutableAccumulator() {
        this(Optional.empty(), Lists.empty());
    }

    @Override
    public Accumulator<Value, Error, Iterable<Error>> withValue(Value node) {
        return new MutableAccumulator<>(Optional.of(node), this.errors);
    }

    @Override
    public Accumulator<Value, Error, Iterable<Error>> withError(Error error) {
        return new MutableAccumulator<>(this.maybeValue, this.errors.add(error));
    }

    @Override
    public <Result extends AttachableToStateResult<Value, Error, Iterable<Error>>> Result match(Function<Value, Result> whenOk, Function<Iterable<Error>, Result> whenErr) {
        return this.maybeValue.map(whenOk)
                .orElseGet(() -> whenErr.apply(this.errors));
    }
}
