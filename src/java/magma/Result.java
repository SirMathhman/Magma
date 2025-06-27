package magma;

import java.util.function.Function;

interface Result<Value, Error> {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);
}
