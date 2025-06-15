package magma.app.maybe.node;

import magma.app.CompileError;
import magma.app.Node;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.rule.or.OrState;

import java.util.List;

public record ErrNodeResult(CompileError error) implements NodeResult {
    @Override
    public NodeListResult addTo(List<Node> list) {
        return new ErrNodeListResult(this.error);
    }

    @Override
    public OrState<Node> attachTo(OrState<Node> state) {
        return state.withError(this.error);
    }
}
