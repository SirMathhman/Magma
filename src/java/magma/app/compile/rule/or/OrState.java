package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.context.Context;

public interface OrState<Value, Error> {
    OrState<Value, Error> withValue(Value node);

    OrState<Value, Error> withError(Error error);

    Result<Value, Error> toResult(Context context);
}
