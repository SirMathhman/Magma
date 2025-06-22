package magma.rule;

import magma.error.CompileError;
import magma.list.ListLike;
import magma.list.ListLikes;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.function.Function;

final class ImmutableAccumulator<Value> implements Accumulator<Value> {
    private final Option<Value> maybeValue;
    private final ListLike<CompileError> errors;

    private ImmutableAccumulator(final Option<Value> maybeValue, final ListLike<CompileError> errors) {
        this.maybeValue = maybeValue;
        this.errors = errors;
    }

    public ImmutableAccumulator() {
        this(new None<>(), ListLikes.empty());
    }

    @Override
    public Accumulator<Value> withValue(final Value value) {
        return new ImmutableAccumulator<Value>(new Some<>(value), this.errors);
    }

    @Override
    public Accumulator<Value> withError(final CompileError error) {
        return new ImmutableAccumulator<Value>(this.maybeValue, this.errors.add(error));
    }

    @Override
    public <Return> Return match(final Function<Value, Return> whenPresent, final Function<ListLike<CompileError>, Return> whenErr) {
        return this.maybeValue.map(whenPresent)
                .orElseGet(() -> whenErr.apply(this.errors));
    }
}
