package magma;

import magma.app.StreamLike;

import java.util.Optional;
import java.util.function.Function;

public interface OptionalLike<Value> {
    static <Value> OptionalLike<Value> empty() {
        return new JavaOptional<>(Optional.empty());
    }

    static <Value> OptionalLike<Value> of(final Value value) {
        return new JavaOptional<>(Optional.of(value));
    }

    <Return> OptionalLike<Return> flatMap(Function<Value, OptionalLike<Return>> mapper);

    <Return> OptionalLike<Return> map(Function<Value, Return> mapper);

    StreamLike<Value> stream();
}
