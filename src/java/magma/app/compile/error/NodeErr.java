package magma.app.compile.error;

import magma.app.compile.node.Node;
import magma.app.compile.rule.or.OrState;

import java.util.function.Supplier;

public record NodeErr(FormattedError node) implements NodeResult {
    @Override
    public NodeResult mergeResult(Supplier<NodeResult> other) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult mergeNode(Node value1) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult retype(String type) {
        return new NodeErr(this.node);
    }

    @Override
    public OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state) {
        return switch (this) {
            case NodeOk(var value) -> state.withValue(value);
            case NodeErr(var error) -> state.withError(error);
        };
    }
}
