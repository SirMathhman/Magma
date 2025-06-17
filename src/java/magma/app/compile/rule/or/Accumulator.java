package magma.app.compile.rule.or;

import magma.app.compile.AttachableToStateResult;

import java.util.List;
import java.util.function.Function;

public interface Accumulator<Value, Error> {
    <Result extends AttachableToStateResult<Value, Error>> Result getMatch(Function<Value, Result> whenOk, Function<List<Error>, Result> whenErr);

    Accumulator<Value, Error> withValue(Value node);

    Accumulator<Value, Error> withError(Error error);
}