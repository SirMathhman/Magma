package magma.result;

import magma.error.IOError;

import java.util.function.Function;

public sealed interface Result<Value> permits Err, Ok {
    <Return> Return match(Function<Value, Return> whenOk, Function<IOError, Return> whenError);

    <Return> Result<Return> map(Function<Value, Return> mapper);

    <Return> Result<Return> flatMap(Function<Value, Result<Return>> mapper);
}
