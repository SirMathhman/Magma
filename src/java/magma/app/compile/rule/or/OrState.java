package magma.app.compile.rule.or;

import magma.app.compile.context.Context;

public interface OrState<Value, Error, Result> {
    OrState<Value, Error, Result> withValue(Value node);

    OrState<Value, Error, Result> withError(Error error);

    Result toResult(Context context);
}
