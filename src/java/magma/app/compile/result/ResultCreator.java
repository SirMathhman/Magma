package magma.app.compile.result;

import magma.app.compile.context.Context;

public interface ResultCreator<Value, Result> {
    Result fromError(Context context);

    Result fromValue(Value value);
}
