package magma.app.compile.rule;

import magma.api.error.list.ErrorList;
import magma.api.error.list.ErrorSequence;
import magma.api.error.list.ImmutableErrorList;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

import java.util.function.Function;

final class ImmutableAccumulator<Value, Error> implements Accumulator<Value, Error> {
    private final Option<Value> maybeValue;
    private final ErrorList<Error> errors;

    private ImmutableAccumulator(final Option<Value> maybeValue, final ErrorList<Error> errors) {
        this.maybeValue = maybeValue;
        this.errors = errors;
    }

    ImmutableAccumulator() {
        this(new None<>(), new ImmutableErrorList<>());
    }

    @Override
    public Accumulator<Value, Error> withValue(final Value value) {
        return new ImmutableAccumulator<>(new Some<>(value), errors);
    }

    @Override
    public Accumulator<Value, Error> withError(final Error error) {
        return new ImmutableAccumulator<>(maybeValue, errors.add(error));
    }

    @Override
    public <Return> Return match(final Function<Value, Return> whenPresent, final Function<ErrorSequence<Error>, Return> whenErr) {
        return maybeValue.map(whenPresent)
                .orElseGet(() -> whenErr.apply(errors));
    }

    @Override
    public boolean hasValue() {
        return maybeValue.isPresent();
    }
}
