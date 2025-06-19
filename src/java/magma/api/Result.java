package magma.api;

import java.util.function.Function;

public interface Result<Value, Error> {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);

    <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);

    <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);
}