package magma.app.maybe;

import magma.app.rule.or.OrState;

public interface Attachable<Value, Error> {
    OrState<Value, Error> attachTo(OrState<Value, Error> state);
}
