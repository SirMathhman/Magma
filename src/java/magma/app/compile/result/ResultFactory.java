package magma.app.compile.result;

import magma.api.result.Result;
import magma.app.compile.context.Context;

public interface ResultFactory<Value, Error> {
    Result<Value, Error> fromError(Context context);

    Result<Value, Error> fromValue(Value value);
}
