package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Ok, Err {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);
}
