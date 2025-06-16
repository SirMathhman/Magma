package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.error.CompileError;

import java.util.List;

public interface OrState<Value> {
    Result<Value, List<CompileError>> toResult();

    OrState<Value> withValue(Value value);

    OrState<Value> withError(CompileError error);
}
