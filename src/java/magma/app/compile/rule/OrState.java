package magma.app.compile.rule;

import magma.api.Result;
import magma.app.compile.error.FormattedError;

import java.util.List;

public interface OrState<Value> {
    Result<Value, List<FormattedError>> toResult();

    OrState<Value> withValue(Value value);

    OrState<Value> withError(FormattedError error);
}
