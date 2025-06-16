package magma.app.compile.rule;

import magma.api.Result;

import java.util.List;

public interface OrState<Value, Error> {
    Result<Value, List<Error>> toResult();

    OrState<Value, Error> withValue(Value value);

    OrState<Value, Error> withError(Error error);
}
