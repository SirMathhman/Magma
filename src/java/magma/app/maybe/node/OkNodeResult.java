package magma.app.maybe.node;

import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.rule.or.OrState;

import java.util.List;

public record OkNodeResult<Node>(Node node) implements NodeResult<Node> {
    @Override
    public NodeListResult<Node> addTo(List<Node> list) {
        list.add(this.node);
        return new PresentNodeListResult<>(list);
    }

    @Override
    public OrState<Node> attachTo(OrState<Node> state) {
        return state.withValue(this.node);
    }
}
