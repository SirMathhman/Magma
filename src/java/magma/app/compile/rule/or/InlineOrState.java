package magma.app.compile.rule.or;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record InlineOrState<Value, Error>(Optional<Value> maybeValue,
                                          List<Error> errors) implements OrState<Value, Error> {
    public InlineOrState() {
        this(Optional.empty(), new ArrayList<>());
    }

    @Override
    public <Return> Return match(Function<Value, Return> whenPresent, Function<List<Error>, Return> whenMissing) {
        return this.maybeValue.map(whenPresent).orElseGet(() -> whenMissing.apply(this.errors));
    }

    @Override
    public OrState<Value, Error> withValue(Value value) {
        return new InlineOrState<>(Optional.of(value), this.errors);
    }

    @Override
    public OrState<Value, Error> withError(Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public boolean hasValue() {
        return this.maybeValue.isPresent();
    }
}