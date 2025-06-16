package magma.app.compile.error;

import magma.app.compile.rule.OrState;

public interface AttachableToState<Node, Error> {
    OrState<Node, Error> attachToState(OrState<Node, Error> nodeState);
}
