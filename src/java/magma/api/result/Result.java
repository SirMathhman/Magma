package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Ok, Err {
    <Result> Result match(Function<Value, Result> whenOk, Function<Error, Result> whenErr);
}
