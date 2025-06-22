package magma.rule;

import magma.list.ListLike;

import java.util.function.Function;

public interface Accumulator<Value, Error> {
    Accumulator<Value, Error> withValue(Value value);

    Accumulator<Value, Error> withError(Error error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<ListLike<Error>, Return> whenErr);

    boolean hasValue();
}
