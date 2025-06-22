package magma.rule;

import magma.error.ErrorList;

import java.util.function.Function;

public interface Accumulator<Value, Error> {
    Accumulator<Value, Error> withValue(Value value);

    Accumulator<Value, Error> withError(Error error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<ErrorList<Error>, Return> whenErr);

    boolean hasValue();
}
