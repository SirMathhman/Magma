package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.error.FormattedError;

public interface OrState<T> {
    OrState<T> withValue(T node);

    OrState<T> withError(FormattedError error);

    Result<T, FormattedError> toResult(Context context);
}
