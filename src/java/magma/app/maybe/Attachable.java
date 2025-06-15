package magma.app.maybe;

import magma.app.rule.OrState;

public interface Attachable<T> {
    OrState<T> attachTo(OrState<T> state);
}
