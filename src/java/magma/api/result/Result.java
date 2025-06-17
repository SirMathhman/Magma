package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Err, Ok {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenError);

    <Return> Result<Return, Error> map(Function<Value, Return> mapper);

    <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper);
}
