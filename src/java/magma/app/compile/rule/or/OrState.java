package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.error.Context;

public interface OrState<Value> {
    OrState<Value> withValue(Value value);

    OrState<Value> withError(CompileError error);

    Result<Value, CompileError> toResult(Context context);
}
