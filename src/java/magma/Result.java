package magma;

import java.io.IOException;
import java.util.function.Function;

public sealed interface Result<Value> permits Err, Ok {
    <Return> Return match(Function<Value, Return> whenOk, Function<IOException, Return> whenError);

    <Return> Result<Return> map(Function<Value, Return> mapper);
}
