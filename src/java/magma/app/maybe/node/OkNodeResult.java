package magma.app.maybe.node;

import magma.app.maybe.NodeListResult;
import magma.app.maybe.NodeResult;
import magma.app.rule.or.OrState;

import java.util.List;

public record OkNodeResult<Node, Error>(Node node) implements NodeResult<Node, Error> {
    @Override
    public NodeListResult<Node, Error> addTo(List<Node> list) {
        list.add(this.node);
        return new PresentNodeListResult<>(list);
    }

    @Override
    public OrState<Node, Error> attachTo(OrState<Node, Error> state) {
        return state.withValue(this.node);
    }
}
