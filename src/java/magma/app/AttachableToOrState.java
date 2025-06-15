package magma.app;

import magma.app.rule.or.OrState;

public interface AttachableToOrState<Value, Error> {
    OrState<Value, Error> attachTo(OrState<Value, Error> state);
}
