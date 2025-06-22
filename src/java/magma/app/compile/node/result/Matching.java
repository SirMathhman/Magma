package magma.app.compile.node.result;

import java.util.function.Function;

public interface Matching<Value, Error> {
    <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenError);
}
