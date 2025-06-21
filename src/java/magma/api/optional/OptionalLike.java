package magma.api.optional;

import magma.api.collect.stream.StreamLike;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OptionalLike<Value> {
    <Return> OptionalLike<Return> flatMap(Function<Value, OptionalLike<Return>> mapper);

    <Return> OptionalLike<Return> map(Function<Value, Return> mapper);

    StreamLike<Value> stream();

    void ifPresent(Consumer<Value> consumer);
}
