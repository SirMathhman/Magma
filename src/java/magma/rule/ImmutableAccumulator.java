package magma.rule;

import magma.list.ListLike;
import magma.list.ListLikes;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.function.Function;

final class ImmutableAccumulator<Value, Error> implements Accumulator<Value, Error> {
    private final Option<Value> maybeValue;
    private final ListLike<Error> errors;

    private ImmutableAccumulator(final Option<Value> maybeValue, final ListLike<Error> errors) {
        this.maybeValue = maybeValue;
        this.errors = errors;
    }

    ImmutableAccumulator() {
        this(new None<>(), ListLikes.empty());
    }

    @Override
    public Accumulator<Value, Error> withValue(final Value value) {
        return new ImmutableAccumulator<>(new Some<>(value), this.errors);
    }

    @Override
    public Accumulator<Value, Error> withError(final Error error) {
        return new ImmutableAccumulator<>(this.maybeValue, this.errors.add(error));
    }

    @Override
    public <Return> Return match(final Function<Value, Return> whenPresent, final Function<ListLike<Error>, Return> whenErr) {
        return this.maybeValue.map(whenPresent)
                .orElseGet(() -> whenErr.apply(this.errors));
    }

    @Override
    public boolean hasValue() {
        return this.maybeValue.isPresent();
    }
}
