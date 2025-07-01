package magma.rule.accumulate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MutableAccumulator<Node, Error> implements Accumulator<Node, Error> {
    private final Optional<Node> maybeValue;
    private final List<Error> errors;

    private MutableAccumulator(final Optional<Node> maybeValue, final List<Error> errors) {
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
    public Accumulator<Node, Error> withValue(final Node value) {
        return new MutableAccumulator<>(Optional.of(value), this.errors);
    }

    @Override
    public Accumulator<Node, Error> withError(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<List<Error>, Return> whenErr) {
        return this.maybeValue.map(whenOk).orElseGet(() -> whenErr.apply(this.errors));
    }
}
