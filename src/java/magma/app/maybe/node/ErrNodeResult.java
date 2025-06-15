package magma.app.maybe.node;

import magma.app.CompileError;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.rule.or.OrState;

import java.util.List;

public final class ErrNodeResult<Node> implements NodeResult<Node> {
    private final CompileError error;

    public ErrNodeResult(CompileError error) {
        this.error = error;
    }

    @Override
    public NodeListResult<Node> addTo(List<Node> list) {
        return new ErrNodeListResult<>(this.error);
    }

    @Override
    public OrState<Node> attachTo(OrState<Node> state) {
        return state.withError(this.error);
    }
}
