package magma.app.compile.rule.or;

import magma.app.compile.AttachableToStateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
    public <Result extends AttachableToStateResult<Value, Error>> Result getMatch(Function<Value, Result> whenOk, Function<List<Error>, Result> whenErr) {
        return this.maybeValue.map(whenOk)
                .orElseGet(() -> whenErr.apply(this.errors));
    }
}
