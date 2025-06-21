package magma.app.optional;

import magma.app.StreamLike;

import java.util.function.Function;

public interface OptionalLike<Value> {
    <Return> OptionalLike<Return> flatMap(Function<Value, OptionalLike<Return>> mapper);

    <Return> OptionalLike<Return> map(Function<Value, Return> mapper);

    StreamLike<Value> stream();
}
