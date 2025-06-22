package magma.app.rule;

import magma.api.error.list.ErrorSequence;

import java.util.function.Function;

public interface Accumulator<Value, Error> {
    Accumulator<Value, Error> withValue(Value value);

    Accumulator<Value, Error> withError(Error error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<ErrorSequence<Error>, Return> whenErr);

    boolean hasValue();
}
