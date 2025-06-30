package magma.rule.accumulate;

import magma.error.CompileError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MutableAccumulator<Node> implements Accumulator<Node> {
    private final Optional<Node> maybeValue;
    private final List<CompileError> errors;

    private MutableAccumulator(final Optional<Node> maybeValue, final List<CompileError> errors) {
        this.maybeValue = maybeValue;
        this.errors = errors;
    }

    public MutableAccumulator() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public boolean isPresent() {
        return this.maybeValue.isPresent();
    }

    @Override
    public Accumulator<Node> withValue(final Node value) {
        return new MutableAccumulator<>(Optional.of(value), this.errors);
    }

    @Override
    public Accumulator<Node> withError(final CompileError error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenOk,
                                 final Function<List<CompileError>, Return> whenErr) {
        return this.maybeValue.map(whenOk).orElseGet(() -> whenErr.apply(this.errors));
    }
}
