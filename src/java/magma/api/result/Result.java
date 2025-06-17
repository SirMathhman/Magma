package magma.api.result;

import java.util.function.Function;

public sealed interface Result<Value, Error> permits Err, Ok {
    <Return> Result<Return, Error> map(Function<Value, Return> mapper);
}
