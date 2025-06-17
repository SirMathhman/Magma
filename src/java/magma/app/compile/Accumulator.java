package magma.app.compile;

import magma.api.list.Streamable;

import java.util.function.Function;

public interface Accumulator<Value, Error> {
    <Result extends AttachableToStateResult<Value, Error>> Result match(Function<Value, Result> whenOk, Function<Streamable<Error>, Result> whenErr);

    Accumulator<Value, Error> withValue(Value node);

    Accumulator<Value, Error> withError(Error error);
}