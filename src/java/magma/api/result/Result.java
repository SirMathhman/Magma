package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Ok, Err {
    <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper);

    <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper);
}
