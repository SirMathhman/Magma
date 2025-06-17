package magma.app.compile.result;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.Context;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;

public class SimpleResultFactory<T> implements ResultFactory<T, Result<T, FormattedError>> {
    @Override
    public Result<T, FormattedError> fromError(Context context) {
        return new Err<>(new CompileError("No combination present", context));
    }

    @Override
    public Result<T, FormattedError> fromValue(T value) {
        return new Ok<>(value);
    }
}