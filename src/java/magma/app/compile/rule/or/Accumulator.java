package magma.app.compile.rule.or;

import magma.api.result.Result;

import java.util.List;

public interface Accumulator<Value, Error> {
    Accumulator<Value, Error> withValue(Value node);

    Accumulator<Value, Error> withError(Error error);

    Result<Value, List<Error>> toResult();
}