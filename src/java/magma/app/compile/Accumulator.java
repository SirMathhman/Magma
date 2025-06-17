package magma.app.compile;

import java.util.function.Function;

public interface Accumulator<Value, Error, Collection> {
    <Result extends AttachableToStateResult<Value, Error>> Result match(Function<Value, Result> whenOk, Function<Collection, Result> whenErr);

    Accumulator<Value, Error, Collection> withValue(Value node);

    Accumulator<Value, Error, Collection> withError(Error error);
}