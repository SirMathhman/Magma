package magma.app.compile.error;

import magma.app.compile.node.Node;
import magma.app.compile.rule.or.OrState;

import java.util.function.Supplier;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public NodeResult mergeResult(Supplier<NodeResult> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult mergeNode(Node value1) {
        return new NodeOk(this.node.merge(value1));
    }

    @Override
    public NodeResult retype(String type) {
        return new NodeOk(this.node.retype(type));
    }

    @Override
    public OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state) {
        return state.withValue(this.node());
    }
}
