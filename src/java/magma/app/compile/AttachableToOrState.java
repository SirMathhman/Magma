package magma.app.compile;

import magma.app.compile.rule.or.OrState;

public interface AttachableToOrState<Value, Error> {
    OrState<Value, Error> attachTo(OrState<Value, Error> state);
}
