package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Err, Ok {
    static <T, X> Result<T, X> fromErr(X error) {
        return new Err<T, X>(error);
    }

    static <T, X> Result<T, X> fromValue(T value) {
        return new Ok<T, X>(value);
    }

    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenError);

    <Return> Result<Return, Error> map(Function<Value, Return> mapper);

    <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper);
}
