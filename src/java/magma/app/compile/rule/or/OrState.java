package magma.app.compile.rule.or;

import java.util.Optional;

public interface OrState<Value, Error> {
    OrState<Value, Error> withValue(Value node);

    OrState<Value, Error> withError(Error error);

    Optional<Value> maybeValue();

}
