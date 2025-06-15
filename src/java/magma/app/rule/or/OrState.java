package magma.app.rule.or;

import java.util.List;
import java.util.function.Function;

public interface OrState<Value, Error> {
    <Return> Return match(Function<Value, Return> whenPresent, Function<List<Error>, Return> whenMissing);

    OrState<Value, Error> withValue(Value value);

    OrState<Value, Error> withError(Error error);

    boolean hasValue();
}
