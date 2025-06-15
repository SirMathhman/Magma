package magma.app.maybe.node;

import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.rule.or.OrState;

import java.util.List;

public final class ErrNodeResult<Node, Error> implements NodeResult<Node, Error> {
    private final Error error;

    public ErrNodeResult(Error error) {
        this.error = error;
    }

    @Override
    public NodeListResult<Node, Error> addTo(List<Node> list) {
        return new ErrNodeListResult<>(this.error);
    }

    @Override
    public OrState<Node, Error> attachTo(OrState<Node, Error> state) {
        return state.withError(this.error);
    }
}
