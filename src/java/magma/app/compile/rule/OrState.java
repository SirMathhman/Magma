package magma.app.compile.rule;

import magma.api.Error;
import magma.api.Result;

import java.util.List;

public interface OrState<Value> {
    Result<Value, List<Error>> toResult();

    OrState<Value> withValue(Value value);

    OrState<Value> withError(Error error);
}
