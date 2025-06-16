package magma.app.compile.error;

import magma.app.compile.rule.OrState;

public interface AttachableToState<Value, Error> {
    OrState<Value, Error> attachToState(OrState<Value, Error> state);
}
