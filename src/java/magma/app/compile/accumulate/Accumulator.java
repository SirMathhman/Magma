package magma.app.compile.accumulate;

import java.util.function.Function;

public interface Accumulator<Value, Error, Errors> {
    Accumulator<Value, Error, Errors> withValue(Value value);

    Accumulator<Value, Error, Errors> withError(Error error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<Errors, Return> whenErr);

    boolean hasValue();
}
