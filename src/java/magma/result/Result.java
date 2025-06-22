package magma.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Err, Ok {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenError);

    <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);

    <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);

    <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper);
}
