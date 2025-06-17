package magma.app.compile.rule.or;

import java.util.Optional;

public interface OrState<Value, Error, Result> {
    OrState<Value, Error, Result> withValue(Value node);

    OrState<Value, Error, Result> withError(Error error);

    Optional<Value> maybeValue();

}
