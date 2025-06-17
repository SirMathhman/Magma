package magma.app.compile;

import java.util.function.Function;

public interface Accumulator<Value, Error, Iterable> {
    <Result extends AttachableToStateResult<Accumulator<Value, Error, Iterable>>> Result match(Function<Value, Result> whenOk, Function<Iterable, Result> whenErr);

    Accumulator<Value, Error, Iterable> withValue(Value node);

    Accumulator<Value, Error, Iterable> withError(Error error);
}