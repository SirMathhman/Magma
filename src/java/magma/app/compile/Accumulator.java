package magma.app.compile;

import magma.api.list.List;

import java.util.function.Function;

public interface Accumulator<Value, Error> {
    <Result extends AttachableToStateResult<Value, Error>> Result getMatch(Function<Value, Result> whenOk, Function<List<Error>, Result> whenErr);

    Accumulator<Value, Error> withValue(Value node);

    Accumulator<Value, Error> withError(Error error);
}