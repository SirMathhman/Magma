package magma.app.compile.rule.or;

import magma.app.compile.CompileError;
import magma.app.compile.error.Context;
import magma.app.compile.rule.result.RuleResult;

public interface OrState<Value> {
    OrState<Value> withValue(Value value);

    OrState<Value> withError(CompileError error);

    RuleResult<Value> toResult(Context context);
}
