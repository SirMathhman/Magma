package magma.result;

import java.util.function.Function;

public interface Matchable<Value, Error> {
    <Return> Return match(Function<Value, Return> whenPresent, Function<Error, Return> whenErr);
}
