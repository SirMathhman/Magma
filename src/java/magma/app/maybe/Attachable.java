package magma.app.maybe;

import magma.app.rule.or.OrState;

public interface Attachable<T> {
    OrState<T> attachTo(OrState<T> state);
}
